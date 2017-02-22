package net.torocraft.chess.engine.checkers.workers;

import java.util.ArrayList;
import java.util.List;

import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.checkers.CheckersMoveResult;
import net.torocraft.chess.engine.checkers.CheckersPieceState;

public abstract class CheckersPieceWorker implements ICheckersPieceWorker {
	protected final List<CheckersPieceState> state;
	protected final CheckersPieceState pieceToMove;
	protected CheckersMoveResult moveResult;
	protected CheckersPieceState[][] positionArray;

	public CheckersPieceWorker(List<CheckersPieceState> state, CheckersPieceState pieceToMove) {
		this.state = state;
		this.pieceToMove = pieceToMove;
		moveResult = new CheckersMoveResult();
		moveResult.legalPositions = new ArrayList<>();
		positionArray = new CheckersPieceState[8][8];
		populatePositionArray();
	}

	private void populatePositionArray() {
		for (CheckersPieceState piece : state) {
			positionArray[piece.position.rank.ordinal()][piece.position.file.ordinal()] = piece;
		}
	}

	protected boolean isSpaceFree(Position position) {
		if (position == null || !(position.rank.ordinal() >= 0 && position.rank.ordinal() < 8)) {
			return false;
		}
		return positionArray[position.rank.ordinal()][position.file.ordinal()] == null;
	}

	protected boolean isEnemyOccupying(Position position) {
		if (position == null || !(position.file.ordinal() >= 0 && position.file.ordinal() < 8)) {
			return false;
		}
		CheckersPieceState pieceState = positionArray[position.rank.ordinal()][position.file.ordinal()];
		return pieceState != null && pieceState.side != null && !pieceState.side.equals(pieceToMove.side);
	}

	protected boolean isSpaceFreeFullCheck(Position positionToCheck) {
		return positionToCheck != null && isSpaceFree(positionToCheck);
	}

	protected boolean isEnemyOccupyingFullCheck(Position positionToCheck) {
		return positionToCheck != null && isEnemyOccupying(positionToCheck);
	}

	protected void addLegalMove(Position position) {
		if (position == null) {
			return;
		}
		moveResult.legalPositions.add(position);
	}

	protected Position tryCreatePosition(int rank, int file) {
		if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
			return new Position(File.values()[file], Rank.values()[rank]);
		}
		return null;
	}
}
