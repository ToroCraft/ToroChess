package net.torocraft.chess.engine.checkers;

import net.torocraft.chess.engine.GameMoveResult;
import net.torocraft.chess.engine.GamePieceState;

import java.util.List;

public class CheckersMoveResult extends GameMoveResult {
	public enum Condition {
		CLEAR
	}

	public List<CheckersPieceState.Position> legalPositions;
	public Condition whiteCondition;
	public Condition blackCondition;
}
