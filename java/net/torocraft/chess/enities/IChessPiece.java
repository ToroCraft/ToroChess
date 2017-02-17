package net.torocraft.chess.enities;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.engine.ChessPieceState.Side;

public interface IChessPiece {

	Side getSide();

	void setSide(Side side);

	String getChessPosition();

	void setChessPosition(String position);

	UUID getGameId();

	void setGameId(UUID id);

	BlockPos getA8();

	void setA8(BlockPos pos);

}
