package net.torocraft.chess.engine;

import java.util.List;

public interface IChessRuleEngine {
	/**
	 *
	 * @param state
	 *            the current board state
	 * @param chessPieceToMove
	 *            the chess board piece with position to calculate possible moves from
	 * @return a list of possible moves, if move are possible an empty list is
	 *         returned
	 */
	List<ChessPieceState.Position> getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove);
}
