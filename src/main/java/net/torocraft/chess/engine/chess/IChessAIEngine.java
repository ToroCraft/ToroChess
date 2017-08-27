package net.torocraft.chess.engine.chess;

import static net.torocraft.chess.engine.chess.ChessPieceState.Move;
import static net.torocraft.chess.engine.chess.ChessPieceState.Side;

import java.util.List;

public interface IChessAIEngine {

  /**
   * @param state the current board state
   * @param sideToMove the side that you wish to get an AI move for
   * @return a list of possible moves, if move are possible an empty list is returned
   */
  Move getAIMove(List<ChessPieceState> state, Side sideToMove);
}
