package net.torocraft.chess.engine.chess.workers;

import static net.torocraft.chess.engine.chess.ChessPieceState.Position;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

public interface IChessPieceWorker {
    ChessMoveResult getLegalMoves();
    boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo);
}
