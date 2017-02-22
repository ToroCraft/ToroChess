package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.IGamePieceWorker;
import net.torocraft.chess.engine.chess.ChessMoveResult;

public interface IChessPieceWorker extends IGamePieceWorker<ChessMoveResult> {
	boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo);
}
