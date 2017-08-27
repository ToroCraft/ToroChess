package net.torocraft.chess.engine.checkers;

import net.torocraft.chess.engine.GamePieceState;

public class CheckersPieceState extends GamePieceState {

  public Type type;

  public enum Type {
    CHECKER, KING
  }

}