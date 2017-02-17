package net.torocraft.chess.gen;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.engine.ChessPieceState.Side;
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
	}

	public void placePieces() {
		placeEntity(new EntityPawn(world), Side.WHITE, "a2");
		placeEntity(new EntityPawn(world), Side.WHITE, "b2");
		placeEntity(new EntityPawn(world), Side.WHITE, "c2");
		placeEntity(new EntityPawn(world), Side.WHITE, "d2");
		placeEntity(new EntityPawn(world), Side.WHITE, "e2");
		placeEntity(new EntityPawn(world), Side.WHITE, "f2");
		placeEntity(new EntityPawn(world), Side.WHITE, "g2");
		placeEntity(new EntityPawn(world), Side.WHITE, "h2");

		placeEntity(new EntityRook(world), Side.WHITE, "a1");
		placeEntity(new EntityKnight(world), Side.WHITE, "b1");
		placeEntity(new EntityBishop(world), Side.WHITE, "c1");
		placeEntity(new EntityKing(world), Side.WHITE, "d1");
		placeEntity(new EntityQueen(world), Side.WHITE, "e1");
		placeEntity(new EntityBishop(world), Side.WHITE, "f1");
		placeEntity(new EntityKnight(world), Side.WHITE, "g1");
		placeEntity(new EntityRook(world), Side.WHITE, "h1");

		placeEntity(new EntityPawn(world), Side.BLACK, "a7");
		placeEntity(new EntityPawn(world), Side.BLACK, "b7");
		placeEntity(new EntityPawn(world), Side.BLACK, "c7");
		placeEntity(new EntityPawn(world), Side.BLACK, "d7");
		placeEntity(new EntityPawn(world), Side.BLACK, "e7");
		placeEntity(new EntityPawn(world), Side.BLACK, "f7");
		placeEntity(new EntityPawn(world), Side.BLACK, "g7");
		placeEntity(new EntityPawn(world), Side.BLACK, "h7");

		placeEntity(new EntityRook(world), Side.BLACK, "a8");
		placeEntity(new EntityKnight(world), Side.BLACK, "b8");
		placeEntity(new EntityBishop(world), Side.BLACK, "c8");
		placeEntity(new EntityKing(world), Side.BLACK, "d8");
		placeEntity(new EntityQueen(world), Side.BLACK, "e8");
		placeEntity(new EntityBishop(world), Side.BLACK, "f8");
		placeEntity(new EntityKnight(world), Side.BLACK, "g8");
		placeEntity(new EntityRook(world), Side.BLACK, "h8");
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

	private void placeEntity(EntityChessPiece e, Side side, String position) {
		int x = a8.getX() + world.rand.nextInt(8);
		int z = a8.getZ() + world.rand.nextInt(8);
		e.setChessPosition(position);
		e.setPosition(x, a8.getY() + 1, z);
		e.setSide(side);
		e.setGameId(gameId);
		e.setA8(a8);
		world.spawnEntity(e);
	}

	private Boolean castSide(Side side) {
		if (Side.BLACK.equals(side)) {
			return true;
		} else {
			return false;
		}
	}

}
