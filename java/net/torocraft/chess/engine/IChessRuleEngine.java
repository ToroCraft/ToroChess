package net.torocraft.chess.engine;

import java.util.List;

import net.torocraft.chess.engine.ChessPieceState.Position;

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
	List<Position> getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove);
}
