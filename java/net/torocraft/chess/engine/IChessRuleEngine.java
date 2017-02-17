package net.torocraft.chess.engine;

import java.util.List;

public interface IChessRuleEngine {
	/**
	 *
	 * @param state
	 *            the current board state
	 * @param chessPieceToMove
	 *            the chess board piece with position to calculate possible moves from
	 * @return a list of possible moves, if no moves are possible an empty list is
	 *         returned
	 */
	MoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove);

}
