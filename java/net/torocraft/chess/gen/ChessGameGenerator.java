package net.torocraft.chess.gen;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.blocks.TileEntityChessControl;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;
import net.torocraft.chess.engine.ChessPieceState.Side;
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

	public ChessGameGenerator(World world, BlockPos a8) {
		if (world == null) {
			throw new NullPointerException("null world");
		}
		if (a8 == null) {
			throw new NullPointerException("null a8");
		}
		this.board = new CheckerBoardGenerator(world, a8);
		this.world = world;
		this.a8 = a8;
	}

	public void generate() {
		if (world.isRemote) {
			return;
		}
		board.generate();
		addWand();
		placePieces();
		saveGameData();
	}

	public void placePieces() {
		placeEntity(new EntityPawn(world), Side.WHITE, File.A, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.B, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.C, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.D, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.E, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.F, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.G, Rank.TWO);
		placeEntity(new EntityPawn(world), Side.WHITE, File.H, Rank.TWO);

		placeEntity(new EntityRook(world), Side.WHITE, File.A, Rank.ONE);
		placeEntity(new EntityKnight(world), Side.WHITE, File.B, Rank.ONE);
		placeEntity(new EntityBishop(world), Side.WHITE, File.C, Rank.ONE);
		placeEntity(new EntityKing(world), Side.WHITE, File.E, Rank.ONE);
		placeEntity(new EntityQueen(world), Side.WHITE, File.D, Rank.ONE);
		placeEntity(new EntityBishop(world), Side.WHITE, File.F, Rank.ONE);
		placeEntity(new EntityKnight(world), Side.WHITE, File.G, Rank.ONE);
		placeEntity(new EntityRook(world), Side.WHITE, File.H, Rank.ONE);

		placeEntity(new EntityPawn(world), Side.BLACK, File.A, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.B, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.C, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.D, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.E, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.F, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.G, Rank.SEVEN);
		placeEntity(new EntityPawn(world), Side.BLACK, File.H, Rank.SEVEN);

		placeEntity(new EntityRook(world), Side.BLACK, File.A, Rank.EIGHT);
		placeEntity(new EntityKnight(world), Side.BLACK, File.B, Rank.EIGHT);
		placeEntity(new EntityBishop(world), Side.BLACK, File.C, Rank.EIGHT);
		placeEntity(new EntityKing(world), Side.BLACK, File.E, Rank.EIGHT);
		placeEntity(new EntityQueen(world), Side.BLACK, File.D, Rank.EIGHT);
		placeEntity(new EntityBishop(world), Side.BLACK, File.F, Rank.EIGHT);
		placeEntity(new EntityKnight(world), Side.BLACK, File.G, Rank.EIGHT);
		placeEntity(new EntityRook(world), Side.BLACK, File.H, Rank.EIGHT);
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

	private void placeEntity(EntityChessPiece e, Side side, File file, Rank rank) {
		int x = a8.getX() + world.rand.nextInt(8);
		int z = a8.getZ() + world.rand.nextInt(8);
		e.setChessPosition(new Position(file, rank));
		e.setPosition(x, a8.getY() + 1, z);
		e.setSide(side);
		e.setGameId(gameId);
		e.setA8(a8);
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

}
