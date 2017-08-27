package net.torocraft.chess.control;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.GamePieceState.Position;

public class MessageLegalMovesResponse implements IMessage {

  public BlockPos controlBlockPos;
  private List<Position> legalPositions;

  public MessageLegalMovesResponse() {

  }

  public MessageLegalMovesResponse(BlockPos controlBlockPos, List<Position> legalPositions) {
    this.controlBlockPos = controlBlockPos;
    this.legalPositions = legalPositions;
  }

  public static void init(int packetId) {
    ToroChess.NETWORK.registerMessage(MessageLegalMovesResponse.Handler.class, MessageLegalMovesResponse.class, packetId, Side.CLIENT);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    try {
      controlBlockPos = BlockPos.fromLong(buf.readLong());

      int count = buf.readInt();
      legalPositions = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        legalPositions.add(Position.unpack(buf.readByte()));
      }
    } catch (Exception e) {
      controlBlockPos = null;
      legalPositions = null;
      e.printStackTrace();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (legalPositions == null) {
      legalPositions = new ArrayList<>();
    }
    if (controlBlockPos == null) {
      throw new NullPointerException("control block is null");
    }

    buf.writeLong(controlBlockPos.toLong());

    buf.writeInt(legalPositions.size());
    for (Position p : legalPositions) {
      buf.writeByte(p.pack());
    }
  }

  public static class Handler implements IMessageHandler<MessageLegalMovesResponse, IMessage> {

    @Override
    public IMessage onMessage(final MessageLegalMovesResponse message, MessageContext ctx) {

      if (message.legalPositions == null || message.controlBlockPos == null) {
        return null;
      }

      IThreadListener mainThread = Minecraft.getMinecraft();

      mainThread.addScheduledTask(new Runnable() {
        @Override
        public void run() {
          CheckerBoardOverlay.INSTANCE.setValidMoves(message.legalPositions);
        }
      });

      return null;
    }
  }

}