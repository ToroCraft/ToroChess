package net.torocraft.chess.engine.chess.impl;

import java.util.ArrayList;
import java.util.List;

import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessMoveResult.Condition;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.workers.BishopWorker;
import net.torocraft.chess.engine.chess.workers.IChessPieceWorker;
import net.torocraft.chess.engine.chess.workers.KingWorker;
import net.torocraft.chess.engine.chess.workers.KnightWorker;
import net.torocraft.chess.engine.chess.workers.PawnWorker;
import net.torocraft.chess.engine.chess.workers.QueenWorker;
import net.torocraft.chess.engine.chess.workers.RookWorker;

//TODO add support for castling
//TODO add support for en passant
//TODO add support for pawn promotion
public class ChessRuleEngine implements IChessRuleEngine {
	private ChessMoveResult moveResult;
	private List<ChessPieceState> internalState;
	private ChessPieceState internalChessPieceToMove;
	private IChessPieceWorker chessPieceWorker;

	@Override
	public Game getGameType() {
		return Game.CHESS;
	}

	@Override
	public ChessMoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
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
			return new ChessMoveResult();
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

	private boolean isAKingInCheckMate() {
		// TODO
		if (isSideInCheckmate(Side.BLACK)) {
			moveResult = new ChessMoveResult();
			moveResult.blackCondition = ChessMoveResult.Condition.CHECKMATE;
			moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
			moveResult.legalPositions = new ArrayList<>();
		} else if (isSideInCheckmate(Side.WHITE)) {
			moveResult = new ChessMoveResult();
			moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
			moveResult.whiteCondition = ChessMoveResult.Condition.CHECKMATE;
			moveResult.legalPositions = new ArrayList<>();
			return true;
		}
		return false;
	}

	private boolean isSideInCheckmate(Side side) {
		// TODO logic checking if side is in checkmate
		return false;
	}

	private boolean isAKingInStalemate() {
		// TODO
		if (isThereAStalemate()) {
			moveResult = new ChessMoveResult();
			moveResult.blackCondition = Condition.STALEMATE;
			moveResult.whiteCondition = Condition.STALEMATE;
			moveResult.legalPositions = new ArrayList<>();
			return true;
		}
		return false;
	}

	private boolean isThereAStalemate() {
		// TODO logic checking if side is in stalemate
		// TODO look at piece wanting to move
		return false;
	}

	private boolean isKingInCheck() {
		// TODO check is king is currently in check for current side
		return false;
	}
}
