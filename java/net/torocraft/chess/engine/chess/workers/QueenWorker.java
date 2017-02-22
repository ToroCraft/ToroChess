package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

public class QueenWorker extends ChessPieceWorker {
    public QueenWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        AdjacentChessPieceWorker adjacentWorker = new AdjacentChessPieceWorker(state,chessPieceToMove);
        DiagonalChessPieceWorker diagonalWorker = new DiagonalChessPieceWorker(state,chessPieceToMove);

        ChessMoveResult adjacentMoves = adjacentWorker.getLegalMoves();
        ChessMoveResult diagonalMoves = diagonalWorker.getLegalMoves();

        moveResult.legalPositions.addAll(adjacentMoves.legalPositions);
        moveResult.legalPositions.addAll(diagonalMoves.legalPositions);

        return moveResult;
    }
}
