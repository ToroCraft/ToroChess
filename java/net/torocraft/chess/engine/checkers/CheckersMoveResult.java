package net.torocraft.chess.engine.checkers;

import java.util.List;

import net.torocraft.chess.engine.GameMoveResult;

public class CheckersMoveResult extends GameMoveResult {
	public enum Condition {
		CLEAR
	}

	public List<CheckersPieceState.Position> legalPositions;
	public Condition whiteCondition;
	public Condition blackCondition;
}
