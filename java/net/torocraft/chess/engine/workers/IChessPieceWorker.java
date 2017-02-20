package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import static net.torocraft.chess.engine.ChessPieceState.Position;

public interface IChessPieceWorker {
    MoveResult getLegalMoves();
    boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo);
}
