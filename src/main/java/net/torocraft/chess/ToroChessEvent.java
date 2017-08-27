package net.torocraft.chess;

import java.util.UUID;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.entities.EntityChessPiece;

public class ToroChessEvent extends Event {

  public static class MoveEvent extends ToroChessEvent {

    protected final World world;
    protected final UUID gameId;
    protected final EntityChessPiece piece;
    protected final Position from;
    protected final Position to;

    public MoveEvent(World world, UUID gameId, EntityChessPiece piece, Position from, Position to) {
      this.world = world;
      this.gameId = gameId;
      this.piece = piece;
      this.from = from;
      this.to = to;
    }

    public World getWorld() {
      return world;
    }

    public EntityChessPiece getPiece() {
      return piece;
    }

    public Position getFrom() {
      return from;
    }

    public Position getTo() {
      return to;
    }

    public UUID getGameId() {
      return gameId;
    }

    public static class Start extends MoveEvent {

      public Start(World world, UUID gameId, EntityChessPiece piece, Position from, Position to) {
        super(world, gameId, piece, from, to);
      }
    }

    public static class Finish extends MoveEvent {

      public Finish(World world, UUID gameId, EntityChessPiece piece, Position from, Position to) {
        super(world, gameId, piece, from, to);
      }
    }

  }

}
