package net.torocraft.chess.engine.checkers.impl;

import net.torocraft.chess.engine.checkers.CheckersMoveResult;
import net.torocraft.chess.engine.checkers.CheckersPieceState;
import net.torocraft.chess.engine.checkers.ICheckersRuleEngine;
import net.torocraft.chess.engine.checkers.workers.CheckerWorker;
import net.torocraft.chess.engine.checkers.workers.ICheckersPieceWorker;
import net.torocraft.chess.engine.checkers.workers.KingWorker;

import java.util.List;

public class CheckersRuleEngine implements ICheckersRuleEngine {
    private CheckersMoveResult moveResult;
    private List<CheckersPieceState> internalState;
    private CheckersPieceState internalPieceToMove;
    private ICheckersPieceWorker pieceWorker;

    @Override
    public CheckersMoveResult getMoves(List<CheckersPieceState> state, CheckersPieceState pieceToMove) {
        internalState = state;
        internalPieceToMove = pieceToMove;

        switch (internalPieceToMove.type) {
            case CHECKER:
                pieceWorker = new CheckerWorker(internalState, internalPieceToMove);
                break;
            case KING:
                pieceWorker = new KingWorker(internalState, internalPieceToMove);
                break;
            default:
                return new CheckersMoveResult();
        }

        getLegalMoveWithWorker();

        return moveResult;
    }

    private void getLegalMoveWithWorker() {
        if (pieceWorker  == null) {
            return;
        }
        moveResult = pieceWorker.getLegalMoves();

        moveResult.blackCondition = CheckersMoveResult.Condition.CLEAR;
        moveResult.whiteCondition = CheckersMoveResult.Condition.CLEAR;
    }
}
