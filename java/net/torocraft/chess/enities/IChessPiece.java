package net.torocraft.chess.enities;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;

public interface IChessPiece {

	public static enum Side {
		WHITE, BLACK
	};

	Side getSide();

	void setSide(Side side);

	String getChessPosition();

	void setChessPosition(String position);

	UUID getGameId();

	void setGameId(UUID id);

	BlockPos getA1Pos();

	void setA1Pos(BlockPos pos);

}
