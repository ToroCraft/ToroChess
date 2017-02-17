package net.torocraft.chess.engine;

import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Move;
import static net.torocraft.chess.engine.ChessPieceState.Side;

public interface IChessAIEngine {
    /**
     *
     * @param state
     *            the current board state
     * @param sideToMove
     *            the side that you wish to get an AI move for
     * @return a list of possible moves, if move are possible an empty list is
     *         returned
     */
    Move getAIMove(List<ChessPieceState> state, Side sideToMove);
}
