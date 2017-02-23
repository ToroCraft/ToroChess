package net.torocraft.chess.gen;

import java.util.UUID;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.pawn.EntityPawn;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;

public class ChessGameGenerator {

	private final CheckerBoardGenerator board;
	private final World world;
	private final BlockPos a8;
	private final UUID gameId = UUID.randomUUID();
	private IBlockState whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
	private IBlockState blackBlock = Blocks.OBSIDIAN.getDefaultState();

	public ChessGameGenerator(World world, BlockPos a8, BlockChessControl.EnumType type) {
		if (world == null) {
			throw new NullPointerException("null world");
		}
		if (a8 == null) {
			throw new NullPointerException("null a8");
		}
		this.board = new CheckerBoardGenerator(world, a8);
		this.world = world;
		this.a8 = a8;
		setBlockType(type);

		System.out.println("block type: " + type);

		System.out.println("white: " + whiteBlock);
		System.out.println("black: " + blackBlock);

	}

	public void generate() {
		if (world.isRemote) {
			return;
		}
		board.setWhiteBlock(whiteBlock);
		board.setBlackBlock(blackBlock);
		board.generate();
		addWand();
		placePieces(world, a8, gameId);
		saveGameData();
	}

	public static void placePieces(World world, BlockPos a8, UUID gameId) {
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.A, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.B, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.C, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.D, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.E, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.F, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.G, Rank.TWO);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.H, Rank.TWO);

		placeEntity(world, a8, gameId, new EntityRook(world), Side.WHITE, File.A, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityKnight(world), Side.WHITE, File.B, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityBishop(world), Side.WHITE, File.C, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityKing(world), Side.WHITE, File.E, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityQueen(world), Side.WHITE, File.D, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityBishop(world), Side.WHITE, File.F, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityKnight(world), Side.WHITE, File.G, Rank.ONE);
		placeEntity(world, a8, gameId, new EntityRook(world), Side.WHITE, File.H, Rank.ONE);

		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.A, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.B, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.C, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.D, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.E, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.F, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.G, Rank.SEVEN);
		placeEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.H, Rank.SEVEN);

		placeEntity(world, a8, gameId, new EntityRook(world), Side.BLACK, File.A, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityKnight(world), Side.BLACK, File.B, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityBishop(world), Side.BLACK, File.C, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityKing(world), Side.BLACK, File.E, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityQueen(world), Side.BLACK, File.D, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityBishop(world), Side.BLACK, File.F, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityKnight(world), Side.BLACK, File.G, Rank.EIGHT);
		placeEntity(world, a8, gameId, new EntityRook(world), Side.BLACK, File.H, Rank.EIGHT);
	}

	private void addWand() {
		for (int i = 0; i < 4; i++) {
			board.getWhiteChest().setInventorySlotContents(i, createWand(Side.WHITE));
			board.getBlackChest().setInventorySlotContents(i, createWand(Side.BLACK));
		}
	}

	private ItemStack createWand(Side side) {
		ItemStack wand = new ItemStack(ItemChessControlWand.INSTANCE, 1);
		NBTTagCompound c = new NBTTagCompound();
		c.setLong(ItemChessControlWand.NBT_A8_POS, a8.toLong());
		c.setBoolean(ItemChessControlWand.NBT_SIDE, castSide(side));
		c.setUniqueId(ItemChessControlWand.NBT_GAME_ID, gameId);
		wand.setTagCompound(c);
		return wand;
	}

	private static void placeEntity(World world, BlockPos a8, UUID gameId, EntityChessPiece e, Side side, File file, Rank rank) {
		int x = a8.getX() + world.rand.nextInt(8);
		int z = a8.getZ() + world.rand.nextInt(8);
		e.setChessPosition(new Position(file, rank));
		e.setPosition(x, a8.getY() + 1, z);
		e.setSide(side);
		e.setGameId(gameId);
		e.setA8(a8);
		e.setInitialMove(true);
		world.spawnEntity(e);
	}

	private void saveGameData() {
		TileEntityChessControl control = BlockChessControl.getChessControl(world, a8);
		control.setGameId(gameId);
		control.setSelectedPiece(null);
		control.setTurn(Side.WHITE);
		control.markDirty();
	}

	private Boolean castSide(Side side) {
		if (Side.BLACK.equals(side)) {
			return true;
		} else {
			return false;
		}
	}

	public void setBlockType(BlockChessControl.EnumType type) {
		switch (type) {
		case DIAMOND_LAPIS:
			whiteBlock = Blocks.DIAMOND_BLOCK.getDefaultState();
			blackBlock = Blocks.LAPIS_BLOCK.getDefaultState();
			return;
		case DIORITE_GRANITE:
			whiteBlock = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
			blackBlock = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
			return;
		case ENDSTONE_GLOWSTONE:
			whiteBlock = Blocks.END_STONE.getDefaultState();
			blackBlock = Blocks.GLOWSTONE.getDefaultState();
			return;
		case GLASS:
			whiteBlock = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.WHITE);
			blackBlock = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLACK);
			return;
		case METAL:
			whiteBlock = Blocks.IRON_BLOCK.getDefaultState();
			blackBlock = Blocks.GOLD_BLOCK.getDefaultState();
			return;
		case NETHER:
			whiteBlock = Blocks.MAGMA.getDefaultState();
			blackBlock = Blocks.NETHERRACK.getDefaultState();
			return;
		case QUARTZ_OBSIDIAN:
			whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
			blackBlock = Blocks.OBSIDIAN.getDefaultState();
			return;
		case WOOD:
			whiteBlock = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH);
			blackBlock = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
			return;
		case WOOL:
			whiteBlock = Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
			blackBlock = Blocks.PLANKS.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
			return;
		default:
			whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
			blackBlock = Blocks.OBSIDIAN.getDefaultState();
			return;
		}
	}

}
