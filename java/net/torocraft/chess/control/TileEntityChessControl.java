package net.torocraft.chess.control;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChessEvent.MoveEvent;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.engine.chess.CastleMove;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.impl.ChessRuleEngine;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.gen.ChessGameGenerator;
import net.torocraft.chess.items.HighlightedChessPiecePredicate;

public class TileEntityChessControl extends TileEntity implements ITickable {

	private static final String NBT_SELECTED_RANK = "chessposrank";
	private static final String NBT_SELECTED_FILE = "chessposfile";
	private static final String NBT_GAME_ID = "chessgameid";
	private static final String NBT_TURN = "chessturn";
	private static final String NBT_A8 = "chessa8";

	private UUID gameId;
	private Position selectedPiece;
	private Side turn = Side.WHITE;
	private IChessRuleEngine ruleEngine;
	private BlockPos a8;
	ChessMoveResult moves;
	private int fireworksRunCounter = -1;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityChessControl.class, "chess_control_tile_entity");
	}

	public TileEntityChessControl() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public IChessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = new ChessRuleEngine();
		}
		return ruleEngine;
	}

	public void resetBoard() {
		if (gameId == null) {
			throw new NullPointerException("gameId is null");
		}
		if (a8 == null) {
			throw new NullPointerException("gameId is null");
		}
		clearBoard();
		turn = Side.WHITE;
		ChessGameGenerator.placePieces(world, a8, gameId);
	}

	public void clearBoard() {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece piece : pieces) {
			piece.setDead();
		}
	}

	public boolean castlePiece(BlockPos a8, Position to) {
		if (selectedPiece == null) {
			return false;
		}

		Position from = selectedPiece;
		EntityChessPiece king = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (king == null) {
			return false;
		}

		if (!(king instanceof EntityKing)) {
			return false;
		}

		if (isNotYourTurn(king)) {
			// System.out.println("It's not " +
			// king.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		EntityChessPiece rook = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (rook == null || !(rook instanceof EntityRook)) {
			return false;
		}

		if (!isSameSide(king, rook)) {
			return false;
		}

		// System.out.println("Request Castle: " + from + " -> " + to);

		if (moves == null) {
			updateValidMoves(king);
		}

		if (moves == null) {
			return false;
		}

		if (moves.kingSideCastleMove != null) {
			if (moves.kingSideCastleMove.positionOfRook.equals(rook.getChessPosition())) {
				startCastle(moves.kingSideCastleMove, rook, king);
				return true;
			}
		}

		if (moves.queenSideCastleMove != null) {
			if (moves.queenSideCastleMove.positionOfRook.equals(rook.getChessPosition())) {
				startCastle(moves.queenSideCastleMove, rook, king);
				return true;
			}
		}

		return false;
	};

	private void startCastle(CastleMove castleMove, EntityChessPiece rook, EntityChessPiece king) {
		deselectEntity();
		rook.setChessPosition(castleMove.positionToMoveRookTo);
		king.setChessPosition(castleMove.positionToMoveKingTo);
		switchTurns();
	}

	public boolean movePiece(BlockPos a8, Position to) {

		if (selectedPiece == null) {
			// System.out.println("No piece has been selected");
			return false;
		}

		Position from = selectedPiece;

		EntityChessPiece attacker = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (attacker == null) {
			return false;
		}

		if (isNotYourTurn(attacker)) {
			// System.out.println("It's not " +
			// attacker.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		// System.out.println("Request Move: " + from + " -> " + to);

		if (isInvalidMove(gameId, a8, from, to)) {
			// System.out.println("INVALID MOVE");
			return false;
		}

		EntityChessPiece victim = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (isSameSide(attacker, victim)) {
			return false;
		}

		deselectEntity();
		switchTurns();

		attacker.setAttackTarget(victim);
		attacker.setChessPosition(to);

		return true;
	}

	public boolean isInvalidMove(UUID gameId, BlockPos a8, Position from, Position to) {
		if (from == null || to == null) {
			return true;
		}

		if (moves == null) {
			return false;
		}

		for (Position move : moves.legalPositions) {
			if (move.equals(to)) {
				return false;
			}
		}
		return true;
	}

	@SubscribeEvent
	public void onMoveFinish(MoveEvent.Finish event) {
		if (gameId == null || !gameId.equals(event.getGameId())) {
			return;
		}
		Side thisSide = event.getPiece().getSide();
		Side otherSide = otherSide(thisSide);
		List<ChessPieceState> boardState = CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8);

		// updateBoardCondition(otherSide, boardState);
		updateBoardCondition_theSlowButWorkingWay(otherSide, boardState);

		handleBoardCondition();
	}

	private void updateBoardCondition_theSlowButWorkingWay(Side otherSide, List<ChessPieceState> boardState) {
		ChessPieceState otherKing = null;
		for (ChessPieceState state : boardState) {
			if (state.side.equals(otherSide) && ChessPieceState.Type.KING.equals(state.type)) {
				otherKing = state;
			}
		}
		moves = getRuleEngine().getMoves(boardState, otherKing);
	}

	@SuppressWarnings("unused")
	private void updateBoardCondition(Side otherSide, List<ChessPieceState> boardState) {
		moves = getRuleEngine().getBoardConditionForSide(boardState, otherSide);
	}

	private Side otherSide(Side thisSide) {
		if (Side.WHITE.equals(thisSide)) {
			return Side.BLACK;
		} else {
			return Side.WHITE;
		}
	}

	private boolean isNotYourTurn(EntityChessPiece attacker) {
		return !attacker.getSide().equals(turn);
	}

	private void switchTurns() {
		if (Side.WHITE.equals(turn)) {
			turn = Side.BLACK;
		} else {
			turn = Side.WHITE;
		}
		// System.out.println(turn.toString().toLowerCase() + "'s turn");
	}

	private boolean isSameSide(EntityChessPiece target, EntityChessPiece victum) {
		return victum != null && victum.getSide().equals(target.getSide());
	}

	public boolean selectEntity(EntityChessPiece target) {
		if (target == null) {
			throw new NullPointerException("target is null");
		}

		if (target.getChessPosition().equals(selectedPiece)) {
			deselectEntity();
			markDirty();
			return true;
		}

		if (isNotYourTurn(target)) {
			// System.out.println("It's not " +
			// target.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		selectedPiece = target.getChessPosition();
		setHightlight(target);
		updateValidMoves(target);
		markDirty();
		return true;
	}

	public void deselectEntity() {
		selectedPiece = null;
		removeAllHighlights();
		markDirty();
	}

	private void setHightlight(EntityChessPiece target) {
		removeAllHighlights();
		target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false));
	}

	private void removeAllHighlights() {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new HighlightedChessPiecePredicate(gameId));
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	private void updateValidMoves(EntityChessPiece piece) {
		moves = null;

		ChessPieceState thisPiece = CheckerBoardUtil.convertToState(piece);
		List<ChessPieceState> allPieces = CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8);

		moves = getRuleEngine().getMoves(allPieces, thisPiece);

		if (moves == null) {
			return;
		}

		if (world.isRemote) {
			updateValidMovesOverlay();
		}

		handleBoardCondition();
	}

	@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
	private void updateValidMovesOverlay() {
		CheckerBoardOverlay.INSTANCE.setValidMoves(moves.legalPositions);
	}

	private void handleBoardCondition() {
		if (moves == null || moves.blackCondition == null || moves.whiteCondition == null) {
			return;
		}

		if (moves.blackCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			initiateCheckmate(Side.BLACK);
			return;
		} else if (moves.whiteCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			initiateCheckmate(Side.WHITE);
			return;
		}

		EntityKing whiteKing = getKing(Side.WHITE);
		EntityKing blackKing = getKing(Side.BLACK);

		if (whiteKing != null) {
			whiteKing.setInCheck(moves.whiteCondition.equals(ChessMoveResult.Condition.CHECK));
		}
		if (blackKing != null) {
			blackKing.setInCheck(moves.blackCondition.equals(ChessMoveResult.Condition.CHECK));
		}
	}

	private EntityKing getKing(Side side) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				Predicates.and(new ChessPieceSearchPredicate(gameId), new KingSelector(side)));

		return pieces == null || pieces.size() < 1 ? null : (EntityKing) pieces.get(0);
	}

	private void initiateCheckmate(Side losingSide) {
		for (ChessPieceState chessPieceState : CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8)) {
			EntityChessPiece chessPiece = CheckerBoardUtil.getPiece(world, chessPieceState.position, a8, gameId);
			if (chessPiece != null && !chessPiece.getSide().equals(losingSide)) {
				chessPiece.initiateWinCondition();
			}
		}
		startFireworkDisplay();
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		if (c.hasKey(NBT_SELECTED_FILE) && c.hasKey(NBT_SELECTED_RANK)) {
			selectedPiece = new Position(File.values()[c.getInteger(NBT_SELECTED_FILE)], Rank.values()[c.getInteger(NBT_SELECTED_RANK)]);
		} else {
			selectedPiece = null;
		}
		gameId = c.getUniqueId(NBT_GAME_ID);
		turn = CheckerBoardUtil.castSide(c.getBoolean(NBT_TURN));
		a8 = BlockPos.fromLong(c.getLong(NBT_A8));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cIn) {
		NBTTagCompound c = super.writeToNBT(cIn);
		if (selectedPiece == null) {
			c.removeTag(NBT_SELECTED_FILE);
			c.removeTag(NBT_SELECTED_RANK);
		} else {
			c.setInteger(NBT_SELECTED_FILE, selectedPiece.file.ordinal());
			c.setInteger(NBT_SELECTED_RANK, selectedPiece.rank.ordinal());
		}

		if (gameId != null) {
			c.setUniqueId(NBT_GAME_ID, gameId);
		}

		c.setBoolean(NBT_TURN, CheckerBoardUtil.castSide(turn));

		if (a8 != null) {
			c.setLong(NBT_A8, a8.toLong());
		}

		return c;
	}

	public UUID getGameId() {
		return gameId;
	}

	public Position getSelectedPiece() {
		return selectedPiece;
	}

	public Side getTurn() {
		return turn;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
		markDirty();
	}

	public void setSelectedPiece(Position selectedPiece) {
		this.selectedPiece = selectedPiece;
		markDirty();
	}

	public void setTurn(Side turn) {
		this.turn = turn;
		markDirty();
	}

	public void setRuleEngine(IChessRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
		markDirty();
	}

	public BlockPos getA8() {
		return a8;
	}

	public void setA8(BlockPos a8) {
		this.a8 = a8;
		markDirty();
	}

	private static class KingSelector implements Predicate<EntityChessPiece> {
		private final Side side;

		public KingSelector(Side side) {
			this.side = side;
		}

		@Override
		public boolean apply(EntityChessPiece piece) {
			return piece != null && piece instanceof EntityKing && side.equals(piece.getSide());
		}
	}

	private void buildFirework(World world, BlockPos pos) {
		ItemStack item = new ItemStack(Items.FIREWORKS);

		NBTTagCompound explosion1 = buildFireworkExplosive(world.rand);

		NBTTagList explosions = new NBTTagList();
		explosions.appendTag(explosion1);

		NBTTagCompound fireworks = new NBTTagCompound();
		fireworks.setTag("Explosions", explosions);
		fireworks.setInteger("Flight", 0 + world.rand.nextInt(2));

		NBTTagCompound c = new NBTTagCompound();
		c.setTag("Fireworks", fireworks);

		item.setTagCompound(c);

		EntityFireworkRocket firework = new EntityFireworkRocket(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, item);

		world.spawnEntity(firework);
	}

	private NBTTagCompound buildFireworkExplosive(Random rand) {
		NBTTagCompound explosion1 = new NBTTagCompound();
		explosion1.setBoolean("Flicker", true);
		explosion1.setBoolean("Trail", true);
		explosion1.setInteger("Type", rand.nextInt(5));
		explosion1.setIntArray("Colors", new int[] { getColor(rand) });
		return explosion1;
	}

	private int getColor(Random rand) {
		switch (rand.nextInt(6)) {
		case 0:
			return 0xFF0000;
		case 1:
			return 0x00FF00;
		case 2:
			return 0xFFFF00;
		case 3:
			return 0x00FFFF;
		case 4:
			return 0xFF00FF;
		case 5:
			return 0x0000FF;
		}
		return 0xFF0000;
	}

	private void startFireworkDisplay() {
		fireworksRunCounter = 0;
	}

	@Override
	public void update() {
		updateFireworks();
	}

	private void updateFireworks() {
		if (fireworksRunCounter < 0) {
			return;
		}

		if (world.getTotalWorldTime() % (2 + world.rand.nextInt(10)) != 0) {
			return;
		}

		buildFirework(world, a8.add(world.rand.nextInt(8), 0, world.rand.nextInt(8)));

		fireworksRunCounter++;

		if (fireworksRunCounter > 30) {
			fireworksRunCounter = -1;
		}
	}

}
