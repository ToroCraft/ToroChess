package net.torocraft.chess.engine.chess;

import static net.torocraft.chess.engine.GamePieceState.Position;

import java.util.List;
import net.torocraft.chess.engine.GameMoveResult;

public class ChessMoveResult extends GameMoveResult {

  public List<Position> legalPositions;
  public Condition whiteCondition;
  public Condition blackCondition;
  public CastleMove queenSideCastleMove;
  public CastleMove kingSideCastleMove;
  public enum Condition {
    CHECKMATE, STALEMATE, CHECK, CLEAR
  }
}
