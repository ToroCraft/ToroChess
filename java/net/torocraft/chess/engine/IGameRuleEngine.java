package net.torocraft.chess.engine;

import java.util.List;

public interface IGameRuleEngine<PieceState extends GamePieceState, MoveResults extends GameMoveResult> {
	/**
	 *
	 * @param state
	 *            the current board state
	 * @param chessPieceToMove
	 *            the chess board piece with position to calculate possible
	 *            moves from
	 * @return a list of possible moves, if no moves are possible an empty list
	 *         is returned
	 */
	MoveResults getMoves(List<PieceState> state, PieceState chessPieceToMove);
}
