package net.torocraft.chess.entities;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Side;

public interface IChessPiece {

	Side getSide();

	void setSide(Side side);

	Position getChessPosition();

	void setChessPosition(Position position);

	UUID getGameId();

	void setGameId(UUID id);

	BlockPos getA8();

	void setA8(BlockPos pos);

	boolean isInitialMove();

	void setInitialMove(boolean initialMove);
}
