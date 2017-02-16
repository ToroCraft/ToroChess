package net.torocraft.chess.engine;

import java.util.List;

public interface IChessRuleEngine {

	/**
	 * 
	 * @param state
	 *            the current board state
	 * @param fromPosition
	 *            the chess board position to calculate possible moves from
	 * @return a list of possible moves, if move are possible an empty list is
	 *         returned
	 */
	List<String> getMoves(List<ChessPieceState> state, String fromPosition);

}
