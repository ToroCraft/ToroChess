package net.torocraft.chess.items;

import com.google.common.base.Predicate;
import java.util.UUID;
import net.minecraft.init.MobEffects;
import net.torocraft.chess.entities.EntityChessPiece;

public class HighlightedChessPiecePredicate implements Predicate<EntityChessPiece> {

  private final UUID gameId;

  public HighlightedChessPiecePredicate(UUID gameId) {
    this.gameId = gameId;
  }

  @Override
  public boolean apply(EntityChessPiece e) {
    if (e.getGameId() == null) {
      return false;
    }
    return e.getGameId().equals(gameId) && e.isPotionActive(MobEffects.GLOWING);
  }
}