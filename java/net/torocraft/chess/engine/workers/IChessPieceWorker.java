package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import java.util.List;

public interface IChessPieceWorker {
    MoveResult getLegalMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove);
    boolean isKingInCheck(List<ChessPieceState> state);
}
