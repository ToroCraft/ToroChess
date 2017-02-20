package net.torocraft.chess.engine.impl;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessRuleEngine;
import net.torocraft.chess.engine.MoveResult;
import net.torocraft.chess.engine.workers.*;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.chess.engine.MoveResult.Condition;
import static net.torocraft.chess.engine.ChessPieceState.Side;

public class ChessRuleEngine implements IChessRuleEngine {
    private MoveResult moveResult;
    private List<ChessPieceState> internalState;
    private ChessPieceState internalChessPieceToMove;
    private IChessPieceWorker chessPieceWorker;

    @Override
    public MoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        internalState = state;
        internalChessPieceToMove = chessPieceToMove;

        if (isAKingInCheckMate()) {
            return moveResult;
        }

        if (isAKingInStalemate()) {
            return moveResult;
        }

        switch (chessPieceToMove.type) {
            case BISHOP:
                chessPieceWorker = new BishopWorker(internalState, internalChessPieceToMove);
                break;
            case KING:
                chessPieceWorker = new KingWorker(internalState, internalChessPieceToMove);
                break;
            case KNIGHT:
                chessPieceWorker = new KnightWorker(internalState, internalChessPieceToMove);
                break;
            case PAWN:
                chessPieceWorker = new PawnWorker(internalState, internalChessPieceToMove);
                break;
            case QUEEN:
                chessPieceWorker = new QueenWorker(internalState, internalChessPieceToMove);
                break;
            case ROOK:
                chessPieceWorker = new RookWorker(internalState, internalChessPieceToMove);
                break;
            default:
                return new MoveResult();
        }

        getLegalMoveWithWorker();

        return moveResult;
    }

    private void getLegalMoveWithWorker() {
        if (chessPieceWorker == null) {
            return;
        }
        moveResult = chessPieceWorker.getLegalMoves();

        if (isKingInCheck()) {
            if (internalChessPieceToMove.side.equals(ChessPieceState.Side.BLACK)) {
                moveResult.blackCondition = MoveResult.Condition.CHECK;
                moveResult.whiteCondition = MoveResult.Condition.CLEAR;
            } else {
                moveResult.blackCondition = MoveResult.Condition.CLEAR;
                moveResult.whiteCondition = MoveResult.Condition.CHECK;
            }
        } else {
            moveResult.blackCondition = MoveResult.Condition.CLEAR;
            moveResult.whiteCondition = MoveResult.Condition.CLEAR;
        }
    }

    private boolean isAKingInCheckMate() {
        //TODO
        if (isSideInCheckmate(Side.BLACK)) {
            moveResult = new MoveResult();
            moveResult.blackCondition = MoveResult.Condition.CHECKMATE;
            moveResult.whiteCondition = MoveResult.Condition.CLEAR;
            moveResult.legalPositions = new ArrayList<>();
        } else if (isSideInCheckmate(Side.WHITE)){
            moveResult = new MoveResult();
            moveResult.blackCondition = MoveResult.Condition.CLEAR;
            moveResult.whiteCondition = MoveResult.Condition.CHECKMATE;
            moveResult.legalPositions = new ArrayList<>();
            return true;
        }
        return false;
    }

    private boolean isSideInCheckmate(Side side) {
        //TODO logic checking if side is in checkmate
        return false;
    }

    private boolean isAKingInStalemate() {
        //TODO
        if (isThereAStalemate()) {
            moveResult = new MoveResult();
            moveResult.blackCondition = Condition.STALEMATE;
            moveResult.whiteCondition = Condition.STALEMATE;
            moveResult.legalPositions = new ArrayList<>();
            return true;
        }
        return false;
    }

    private boolean isThereAStalemate() {
        //TODO logic checking if side is in stalemate
        //TODO look at piece wanting to move
        return false;
    }

    private boolean isKingInCheck() {
        //TODO check is king is currently in check for current side
        return false;
    }
}
