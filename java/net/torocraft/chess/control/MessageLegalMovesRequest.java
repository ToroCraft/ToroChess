package net.torocraft.chess.control;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.chess.ChessMoveResult;

public class MessageLegalMovesRequest implements IMessage {

  public BlockPos controlBlockPos;

  public MessageLegalMovesRequest() {

  }

  public MessageLegalMovesRequest(BlockPos controlBlockPos) {
    this.controlBlockPos = controlBlockPos;
  }

  public static void init(int packetId) {
    ToroChess.NETWORK.registerMessage(MessageLegalMovesRequest.Handler.class, MessageLegalMovesRequest.class, packetId, Side.SERVER);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    try {
      controlBlockPos = BlockPos.fromLong(buf.readLong());
    } catch (Exception e) {
      controlBlockPos = null;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (controlBlockPos == null) {
      throw new NullPointerException("control block is null");
    }
    buf.writeLong(controlBlockPos.toLong());
  }

  public static class Handler implements IMessageHandler<MessageLegalMovesRequest, IMessage> {

    @Override
    public IMessage onMessage(final MessageLegalMovesRequest message, MessageContext ctx) {
      if (message.controlBlockPos == null) {
        return null;
      }
      final EntityPlayerMP payer = ctx.getServerHandler().playerEntity;
      payer.getServerWorld().addScheduledTask(new Worker(payer, message));
      return null;
    }
  }

  private static class Worker implements Runnable {

    private final EntityPlayerMP player;
    private final MessageLegalMovesRequest message;

    public Worker(EntityPlayerMP player, MessageLegalMovesRequest message) {
      this.player = player;
      this.message = message;
    }

    @Override
    public void run() {
      TileEntityChessControl te = (TileEntityChessControl) player.world.getTileEntity(message.controlBlockPos);

      if (te == null) {
        return;
      }

      ChessMoveResult moves = te.getMoves();

      if (moves == null || moves.legalPositions == null || moves.legalPositions.size() < 1) {
        return;
      }
      ToroChess.NETWORK.sendTo(new MessageLegalMovesResponse(te.getPos(), moves.legalPositions), player);
    }

  }

}