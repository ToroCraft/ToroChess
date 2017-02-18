package net.torocraft.chess.items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.CheckerBoardOverlay;
import net.torocraft.chess.ChessPieceSearchPredicate;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.engine.ChessPieceState.Type;
import net.torocraft.chess.engine.IChessRuleEngine;
import net.torocraft.chess.engine.MoveResult;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.gen.CheckerBoardUtil;

public class ItemChessControlWand extends Item {

	public static final String NBT_SELECTED_RANK = "chessposrank";
	public static final String NBT_SELECTED_FILE = "chessposfile";
	public static final String NBT_SIDE = "chessside";
	public static final String NBT_A8_POS = "chessa8";
	public static final String NBT_GAME_ID = "chessgameid";
	public static final String NAME = "chess_control_wand";
	public static final ModelResourceLocation MODEL_BLACK = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_black", "inventory");
	public static final ModelResourceLocation MODEL_WHITE = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_white", "inventory");

	public static ItemChessControlWand INSTANCE;

	public static void init() {
		INSTANCE = new ItemChessControlWand();
		GameRegistry.register(INSTANCE, new ResourceLocation(ToroChess.MODID, NAME));
	}

	public static void registerRenders() {
		ModelLoader.setCustomMeshDefinition(INSTANCE, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (Side.WHITE.equals(getSide(stack))) {
					return MODEL_WHITE;
				} else {
					return MODEL_BLACK;
				}
			}
		});
		ModelLoader.registerItemVariants(INSTANCE, new ModelResourceLocation[] { MODEL_WHITE, MODEL_BLACK });
	}

	public ItemChessControlWand() {
		setUnlocalizedName(NAME);
		setMaxDamage(1);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {

		ItemStack wand = player.getHeldItem(hand);

		if (world.isRemote || !wand.hasTagCompound()) {
			return EnumActionResult.PASS;
		}

		NBTTagCompound c = wand.getTagCompound();

		if (!c.hasKey(NBT_A8_POS) || !c.hasKey(NBT_SELECTED_FILE) || !c.hasKey(NBT_SELECTED_RANK) || !c.hasKey(NBT_SIDE)) {
			return EnumActionResult.PASS;
		}

		BlockPos a8 = BlockPos.fromLong(wand.getTagCompound().getLong(NBT_A8_POS));
		Position from = getSelectedPiece(wand);
		Position to = CheckerBoardUtil.getChessPosition(a8, pos);
		Side side = castSide(c.getBoolean(NBT_SIDE));
		UUID gameId = c.getUniqueId(NBT_GAME_ID);

		if (gameId == null) {
			return EnumActionResult.PASS;
		}

		movePiece(world, wand, gameId, a8, side, from, to);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (player.world.isRemote || !(target instanceof EntityChessPiece)) {
			return false;
		}

		EntityChessPiece piece = (EntityChessPiece) target;
		ItemStack wand = player.getHeldItem(hand);

		NBTTagCompound c = wand.getTagCompound();
		if (!c.hasKey(NBT_A8_POS) || !c.hasKey(NBT_SIDE)) {
			return false;
		}

		UUID gameId = c.getUniqueId(NBT_GAME_ID);

		if (gameId == null || !gameId.equals(piece.getGameId())) {
			return false;
		}

		Side side = castSide(c.getBoolean(NBT_SIDE));

		if (!side.equals(piece.getSide())) {
			return handleClickOnEnemy(player.world, wand, piece);
		} else {
			return handleClickOnFriend(wand, piece);
		}

	}

	private boolean handleClickOnEnemy(World world, ItemStack wand, EntityChessPiece enemyPiece) {
		Position from = ItemChessControlWand.getSelectedPiece(wand); // wand.getTagCompound().getString(NBT_SELECTED_POS);
		if (from == null) {
			return false;
		}

		BlockPos a8 = BlockPos.fromLong(wand.getTagCompound().getLong(NBT_A8_POS));
		Position to = enemyPiece.getChessPosition();
		Side side = castSide(wand.getTagCompound().getBoolean(NBT_SIDE));
		UUID gameId = wand.getTagCompound().getUniqueId(NBT_GAME_ID);

		if (gameId == null) {
			return false;
		}

		movePiece(world, wand, gameId, a8, side, from, to);
		return true;
	}

	private static Position getSelectedPiece(ItemStack wand) {
		if (wand.hasTagCompound() || !wand.getTagCompound().hasKey(NBT_SELECTED_FILE) || !wand.getTagCompound().hasKey(NBT_SELECTED_RANK)) {
			return null;
		}

		NBTTagCompound c = wand.getTagCompound();
		File file = File.values()[c.getInteger(NBT_SELECTED_FILE)];
		Rank rank = Rank.values()[c.getInteger(NBT_SELECTED_RANK)];

		return new Position(file, rank);
	}

	private boolean handleClickOnFriend(ItemStack stack, EntityChessPiece friendlyPiece) {
		if (friendlyPiece.isPotionActive(MobEffects.GLOWING)) {
			friendlyPiece.removeActivePotionEffect(MobEffects.GLOWING);
			clearSelectedNbt(stack);

		} else {
			highlightEntity(friendlyPiece);
			setSelectedNbt(stack, friendlyPiece);
			onPieceSelected(friendlyPiece);
		}
		return true;
	}

	private static void movePiece(World world, ItemStack stack, UUID gameId, BlockPos a8, Side side, Position from, Position to) {
		EntityChessPiece attacker = getHighlightedPiece(world, from, a8, gameId);

		if (attacker == null) {
			return;
		}

		EntityChessPiece victum = getPiece(world, to, a8, gameId);
		if (victum != null && victum.getSide().equals(side)) {
			victum = null;
			return;
		}

		attacker.removeActivePotionEffect(MobEffects.GLOWING);
		clearSelectedNbt(stack);

		attacker.setAttackTarget(victum);
		attacker.setChessPosition(to);
	}

	private static EntityChessPiece getHighlightedPiece(World world, Position piecePos, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.toWorldCoords(a8, piecePos)).expand(80, 20, 80), new HighlightedChessPiecePredicate(gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static EntityChessPiece getPiece(World world, Position piecePos, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.toWorldCoords(a8, piecePos)).expand(80, 20, 80), new ChessPieceAtPredicate(piecePos, gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static void setSelectedNbt(ItemStack wand, EntityChessPiece target) {
		NBTTagCompound c = wand.getTagCompound();
		Position p = ((EntityChessPiece) target).getChessPosition();
		c.setInteger(NBT_SELECTED_FILE, p.file.ordinal());
		c.setInteger(NBT_SELECTED_RANK, p.rank.ordinal());
		wand.setTagCompound(c);
	}

	private static void clearSelectedNbt(ItemStack wand) {
		NBTTagCompound c = wand.getTagCompound();
		c.removeTag(NBT_SELECTED_FILE);
		c.removeTag(NBT_SELECTED_RANK);
	}

	private static void highlightEntity(EntityChessPiece target) {
		removeAllHighlights(target.world, target.getPosition(), target.getGameId());
		PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false);
		target.addPotionEffect(potioneffect);
	}

	private static void removeAllHighlights(World world, BlockPos pos, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new HighlightedChessPiecePredicate(gameId));
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	private static class HighlightedChessPiecePredicate implements Predicate<EntityChessPiece> {

		private final UUID gameId;

		public HighlightedChessPiecePredicate(UUID gameId) {
			this.gameId = gameId;
		}

		@Override
		public boolean apply(EntityChessPiece e) {
			if (e.getGameId() == null) {
				return false;
			}
			return e.getGameId().equals(gameId) && e.isPotionActive(MobEffects.GLOWING);
		}
	};

	private static class ChessPieceAtPredicate implements Predicate<EntityChessPiece> {
		private final Position chessPosition;
		private final UUID gameId;

		public ChessPieceAtPredicate(Position chessPosition, UUID gameId) {
			this.chessPosition = chessPosition;
			this.gameId = gameId;
		}

		@Override
		public boolean apply(EntityChessPiece e) {
			if (e.getChessPosition() == null || e.getGameId() == null) {
				return false;
			}
			return e.getChessPosition().equals(chessPosition) && e.getGameId().equals(gameId);
		}
	};

	private static Side castSide(Boolean side) {
		if (side != null && side) {
			return Side.BLACK;
		} else {
			return Side.WHITE;
		}
	}

	public static BlockPos getA8(ItemStack stack) {
		return BlockPos.fromLong(stack.getTagCompound().getLong(ItemChessControlWand.NBT_A8_POS));
	}

	public static Side getSide(ItemStack stack) {
		Boolean b = null;
		if (stack.hasTagCompound()) {
			b = stack.getTagCompound().getBoolean(NBT_SIDE);
		}
		return castSide(b);
	}

	private IChessRuleEngine ruleEngine;

	private IChessRuleEngine getRuleEngine() {
		/*
		 * testing rule engine
		 */
		if (ruleEngine == null) {
			ruleEngine = new IChessRuleEngine() {
				@Override
				public MoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
					MoveResult r = new MoveResult();
					r.legalPositions = new ArrayList<>();
					for (ChessPieceState pieceState : state) {
						r.legalPositions.add(pieceState.position);
					}
					return r;
				}
			};
		}

		return ruleEngine;
	}

	private void onPieceSelected(EntityChessPiece friendlyPiece) {
		System.out.println("piece on " + friendlyPiece.getChessPosition() + " selected");
		MoveResult moves = getRuleEngine().getMoves(loadPiecesFromWorld(friendlyPiece), convertToState(friendlyPiece));
		CheckerBoardOverlay.INSTANCE.setValidMoves(moves.legalPositions);
	}

	private static ChessPieceState convertToState(EntityChessPiece entity) {
		ChessPieceState state = new ChessPieceState();

		if (entity instanceof EntityBishop) {
			state.type = Type.BISHOP;

		} else if (entity instanceof EntityKing) {
			state.type = Type.KING;

		} else if (entity instanceof EntityKnight) {
			state.type = Type.KNIGHT;

		} else if (entity instanceof EntityQueen) {
			state.type = Type.QUEEN;

		} else if (entity instanceof EntityRook) {
			state.type = Type.ROOK;

		} else {
			state.type = Type.PAWN;
		}

		state.side = entity.getSide();
		state.position = entity.getChessPosition();
		// TODO: implement this
		state.isInitialMove = false;

		return state;
	}

	public static List<ChessPieceState> loadPiecesFromWorld(EntityChessPiece referenceEntity) {

		World world = referenceEntity.world;
		UUID gameId = referenceEntity.getGameId();
		BlockPos a8 = referenceEntity.getA8();

		List<ChessPieceState> pieces = new ArrayList<>();

		List<EntityChessPiece> entityPieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(a8.add(4, 0, 4)).expand(80, 20, 80), new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece entityPiece : entityPieces) {
			pieces.add(convertToState(entityPiece));
		}

		return pieces;
	}
}
