package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

public class BishopWorker extends ChessPieceWorker {
    public BishopWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        DiagonalChessPieceWorker diagonalWorker = new DiagonalChessPieceWorker(state, chessPieceToMove);
        moveResult = diagonalWorker.getLegalMoves();
        return moveResult;
    }
}
