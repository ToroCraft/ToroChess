package net.torocraft.chess.engine.chess;

import java.util.List;

import net.torocraft.chess.engine.GameMoveResult;

public class ChessMoveResult extends GameMoveResult {
	public enum Condition {
		CHECKMATE, STALEMATE, CHECK, CLEAR
	}

	public List<ChessPieceState.Position> legalPositions;
	public Condition whiteCondition;
	public Condition blackCondition;
}
