package net.torocraft.chess.control;

import com.google.common.base.Predicate;
import java.util.UUID;
import net.torocraft.chess.entities.EntityChessPiece;

public class ChessPieceSearchPredicate implements Predicate<EntityChessPiece> {

  private final UUID gameId;

  public ChessPieceSearchPredicate(UUID gameId) {
    this.gameId = gameId;
  }

  @Override
  public boolean apply(EntityChessPiece e) {
    if (e.getChessPosition() == null || e.getGameId() == null) {
      return false;
    }
    return e.getGameId().equals(gameId);
  }
}