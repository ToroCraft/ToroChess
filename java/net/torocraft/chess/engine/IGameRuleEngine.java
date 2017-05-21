package net.torocraft.chess.engine;

import java.util.List;

public interface IGameRuleEngine<PieceState extends GamePieceState, MoveResults extends GameMoveResult> {

  Game getGameType();

  MoveResults getMoves(List<PieceState> state, PieceState chessPieceToMove);

  /**
   * @param state the current board state
   * @param chessPieceToMove the chess board piece with position to calculate possible moves from
   * @return a list of possible moves, if no moves are possible an empty list is returned
   */

  public enum Game {
    CHECKERS, CHESS
  }
}
