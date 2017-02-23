package net.torocraft.chess.engine.chess.impl;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.workers.*;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.chess.engine.chess.ChessPieceState.Side;
import static net.torocraft.chess.engine.chess.ChessPieceState.Type;
import static net.torocraft.chess.engine.GamePieceState.Position;
import static net.torocraft.chess.engine.chess.ChessMoveResult.Condition;

//TODO add support for castling
//TODO add support for en passant
//TODO add support for pawn promotion
public class ChessRuleEngine implements IChessRuleEngine {
	private ChessMoveResult moveResult;
	private List<ChessPieceState> internalState;
	private ChessPieceState internalChessPieceToMove;
    private ChessPieceState currentKingState;
	private boolean isKingInCheck = false;

	@Override
	public ChessMoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
		internalState = state;
		internalChessPieceToMove = chessPieceToMove;

        currentKingState = getCurrentKingState();
		isKingInCheck = isKingInCheck();
		if (isKingInCheckMate() || isKingInStalemate()) {
			return moveResult;
		}

		IChessPieceWorker chessPieceWorker = getChessPieceWorker(internalChessPieceToMove);
		if (chessPieceWorker == null) {
			return new ChessMoveResult();
		}
		moveResult = chessPieceWorker.getLegalMoves();

		updateBoardCondition();

		System.out.println("\nCURRENT BOARD STATE:" +
				"\nBlack: " + moveResult.blackCondition.toString() +
				"\n White: " + moveResult.whiteCondition.toString());
		return moveResult;
	}

	private ChessPieceState getCurrentKingState() {
	    for (ChessPieceState currentChessPieceState : internalState) {
	        if (currentChessPieceState.side.equals(internalChessPieceToMove.side)
                    && currentChessPieceState.type.equals(Type.KING)) {
	            return currentChessPieceState;
            }
        }
        return null;
    }

	private ChessPieceWorker getChessPieceWorker(ChessPieceState chessPieceToCheck) {
		if (chessPieceToCheck == null) {
			return null;
		}
		switch (chessPieceToCheck.type) {
			case BISHOP:
				return new BishopWorker(internalState, chessPieceToCheck);
			case KING:
				return new KingWorker(internalState, chessPieceToCheck);
			case KNIGHT:
				return new KnightWorker(internalState, chessPieceToCheck);
			case PAWN:
				return new PawnWorker(internalState, chessPieceToCheck);
			case QUEEN:
				return new QueenWorker(internalState, chessPieceToCheck);
			case ROOK:
				return new RookWorker(internalState, chessPieceToCheck);
			default:
				return null;
		}
	}

	private void updateBoardCondition() {
		if (isKingInCheck) {
			if (internalChessPieceToMove.side.equals(ChessPieceState.Side.BLACK)) {
				moveResult.blackCondition = ChessMoveResult.Condition.CHECK;
				moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
			} else {
				moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.whiteCondition = ChessMoveResult.Condition.CHECK;
			}
		} else {
			moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
			moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
		}
	}

	private boolean isKingInCheckMate() {
		if (isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
			if (internalChessPieceToMove.side.equals(Side.BLACK)) {
				moveResult = new ChessMoveResult();
				moveResult.blackCondition = ChessMoveResult.Condition.CHECKMATE;
				moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.legalPositions = new ArrayList<>();
			} else {
				moveResult = new ChessMoveResult();
				moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.whiteCondition = ChessMoveResult.Condition.CHECKMATE;
				moveResult.legalPositions = new ArrayList<>();
			}
			return true;
		}
		return false;
	}

	private boolean isKingInStalemate() {
		if (!isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
			moveResult = new ChessMoveResult();
			moveResult.blackCondition = Condition.STALEMATE;
			moveResult.whiteCondition = Condition.STALEMATE;
			moveResult.legalPositions = new ArrayList<>();
			return true;
		}
		return false;
	}

	private boolean areAnyLegalMovesForCurrentSide() {
		for (ChessPieceState chessPieceState : internalState) {
			if (chessPieceState.side.equals(internalChessPieceToMove.side)) {
				ChessMoveResult moveResult = getChessPieceWorker(chessPieceState).getLegalMoves();
				if (moveResult.legalPositions.size() > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isKingInCheck() {
		for (ChessPieceState chessPieceState : internalState) {
			if (!chessPieceState.side.equals(internalChessPieceToMove.side)) {
				ChessMoveResult moveResult = getChessPieceWorker(chessPieceState).getLegalMoves();
				for (Position position : moveResult.legalPositions) {
				    if (position.rank.equals(currentKingState.position.rank)
                            && position.file.equals(currentKingState.position.file)) {
				        return true;
                    }
                }
			}
		}
		return false;
	}
}
