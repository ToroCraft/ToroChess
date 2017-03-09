package net.torocraft.chess.engine.chess;

import java.util.List;

import net.torocraft.chess.engine.GameMoveResult;
import static net.torocraft.chess.engine.GamePieceState.Position;

public class ChessMoveResult extends GameMoveResult {
	public enum Condition {
		CHECKMATE, STALEMATE, CHECK, CLEAR
	}

	public List<Position> legalPositions;
	public Condition whiteCondition;
	public Condition blackCondition;
    public CastleMove queenSideCastleMove;
    public CastleMove kingSideCastleMove;
}
