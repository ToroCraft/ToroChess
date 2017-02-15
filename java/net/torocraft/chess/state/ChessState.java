package net.torocraft.chess.state;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.enities.EntityChessPiece;

public class ChessState {

	private final List<ChessPieceState> pieces;

	public ChessState(List<ChessPieceState> pieces) {
		this.pieces = pieces;
	}

	public static ChessState loadStateFromWorld(World world, UUID gameId, BlockPos a8) {
		List<ChessPieceState> statePieces = new ArrayList<>();

		List<EntityChessPiece> entityPieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(a8.add(4, 0, 4)).expand(80, 20, 80), new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece entityPiece : entityPieces) {
			statePieces.add(ChessPieceState.fromEntity(entityPiece));
		}

		return new ChessState(statePieces);
	}

	public void readFromNBT(NBTTagCompound compound) {
		// TODO
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return new NBTTagCompound();
		// TODO
	};
}
