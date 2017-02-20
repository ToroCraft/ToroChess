package net.torocraft.chess.engine.impl;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessRuleEngine;
import net.torocraft.chess.engine.MoveResult;
import net.torocraft.chess.engine.workers.*;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.File;
import static net.torocraft.chess.engine.ChessPieceState.Rank;

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

        //FIXME Test data return
        moveResult.legalPositions = new ArrayList<>();
        moveResult.legalPositions.add(new Position(File.A, Rank.FOUR));
        moveResult.legalPositions.add(new Position(File.B, Rank.SEVEN));
        moveResult.blackCondition = MoveResult.Condition.CLEAR;
        moveResult.whiteCondition = MoveResult.Condition.CHECK;
        return moveResult;
    }

    private void getLegalMoveWithWorker() {
        if (chessPieceWorker == null) {
            return;
        }
        if (chessPieceWorker.isKingInCheck(internalState)) {
            moveResult = new MoveResult();
            if (internalChessPieceToMove.side.equals(ChessPieceState.Side.BLACK)) {
                moveResult.blackCondition = MoveResult.Condition.CHECK;
                moveResult.whiteCondition = MoveResult.Condition.CLEAR;
            } else {
                moveResult.blackCondition = MoveResult.Condition.CLEAR;
                moveResult.whiteCondition = MoveResult.Condition.CHECK;
            }
        }
        moveResult = chessPieceWorker.getLegalMoves(internalState, internalChessPieceToMove);
    }

    private boolean isAKingInCheckMate() {
        //TODO
        if (isSideInCheckmate(ChessPieceState.Side.BLACK)) {
            moveResult = new MoveResult();
            moveResult.blackCondition = MoveResult.Condition.CHECKMATE;
            moveResult.whiteCondition = MoveResult.Condition.CLEAR;
            moveResult.legalPositions = new ArrayList<>();
        } else if (isSideInCheckmate(ChessPieceState.Side.WHITE)){
            moveResult = new MoveResult();
            moveResult.blackCondition = MoveResult.Condition.CLEAR;
            moveResult.whiteCondition = MoveResult.Condition.CHECKMATE;
            moveResult.legalPositions = new ArrayList<>();
            return true;
        }
        return false;
    }

    private boolean isSideInCheckmate(ChessPieceState.Side side) {
        //TODO logic checking if side is in checkmate
        return true;
    }

    private boolean isAKingInStalemate() {
        //TODO
        if (isThereAStalemate()) {
            moveResult = new MoveResult();
            moveResult.blackCondition = MoveResult.Condition.STALEMATE;
            moveResult.whiteCondition = MoveResult.Condition.STALEMATE;
            moveResult.legalPositions = new ArrayList<>();
            return true;
        }
        return false;
    }

    private boolean isThereAStalemate() {
        //TODO logic checking if side is in stalemate
        //TODO look at piece wanting to move
        return true;
    }
}
