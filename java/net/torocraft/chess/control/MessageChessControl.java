package net.torocraft.chess.control;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.chess.ToroChess;

public class MessageChessControl implements IMessage {

  public static final int COMMAND_CLEAR = 0;
  public static final int COMMAND_RESET = 1;

  public BlockPos controlBlockPos;
  public int command;

  public MessageChessControl() {

  }

  public MessageChessControl(BlockPos controlBlockPos, int command) {
    this.controlBlockPos = controlBlockPos;
    this.command = command;
  }

  public static void init(int packetId) {
    ToroChess.NETWORK.registerMessage(MessageChessControl.Handler.class, MessageChessControl.class, packetId, Side.SERVER);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    controlBlockPos = BlockPos.fromLong(buf.readLong());
    command = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (controlBlockPos == null) {
      buf.writeLong(0);
    } else {
      buf.writeLong(controlBlockPos.toLong());
    }
    buf.writeInt(command);
  }

  public static class Handler implements IMessageHandler<MessageChessControl, IMessage> {

    @Override
    public IMessage onMessage(final MessageChessControl message, MessageContext ctx) {
      final EntityPlayerMP payer = ctx.getServerHandler().playerEntity;
      payer.getServerWorld().addScheduledTask(new Worker(payer, message));
      return null;
    }
  }

  private static class Worker implements Runnable {

    private final EntityPlayerMP player;
    private final MessageChessControl message;

    public Worker(EntityPlayerMP player, MessageChessControl message) {
      this.player = player;
      this.message = message;
    }

    @Override
    public void run() {
      TileEntityChessControl te = (TileEntityChessControl) player.world.getTileEntity(message.controlBlockPos);

      if (message.command == COMMAND_CLEAR) {
        te.clearBoard();
        return;
      }

      if (message.command == COMMAND_RESET) {
        te.resetBoard();
        return;
      }

    }

  }

}