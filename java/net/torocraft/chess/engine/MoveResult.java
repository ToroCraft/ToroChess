package net.torocraft.chess.engine;

import java.util.List;

public class MoveResult {
	public enum Condition {CHECKMATE, STALEMATE, CHECK, CLEAR}
	public List<ChessPieceState.Position> legalPositions;
	public Condition whiteCondition;
	public Condition blackCondition;
}
