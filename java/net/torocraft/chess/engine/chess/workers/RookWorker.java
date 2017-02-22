package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

public class RookWorker extends ChessPieceWorker {
    public RookWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        AdjacentChessPieceWorker adjacentWorker = new AdjacentChessPieceWorker(state, chessPieceToMove);
        moveResult = adjacentWorker.getLegalMoves();
        return moveResult;
    }
}
