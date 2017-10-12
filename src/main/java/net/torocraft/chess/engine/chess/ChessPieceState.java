package net.torocraft.chess.engine.chess;

import net.torocraft.chess.engine.GamePieceState;

public class ChessPieceState extends GamePieceState {

  public Type type;

  public ChessPieceState() {

  }

  public ChessPieceState(ChessPieceState chessPieceState) {
    this.type = chessPieceState.type;
    this.side = chessPieceState.side;
    this.isInitialMove = chessPieceState.isInitialMove;
    this.position = new Position(chessPieceState.position);
  }

  public enum Type {
    PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
  }

  public int getRanking() {
    switch (type) {
      case PAWN:
        return 1;
      case BISHOP:
        return 2;
      case KNIGHT:
        return 2;
      case ROOK:
        return 3;
      case QUEEN:
        return 4;
      case KING:
        return 5;
      default:
        return 0;
    }
  }
}