package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import java.util.List;

public abstract class ChessPieceWorker implements IChessPieceWorker {
    private MoveResult moveResult;
    private final List<ChessPieceState> state;
    private final ChessPieceState chessPieceToMove;

    public ChessPieceWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        this.state = state;
        this.chessPieceToMove = chessPieceToMove;
        moveResult = new MoveResult();
    }

    @Override
    public boolean isKingInCheck(List<ChessPieceState> updatedState) {
        //TODO, check updated state for king in check
        return true;
    }
}
