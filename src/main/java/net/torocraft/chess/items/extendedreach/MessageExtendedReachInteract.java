package net.torocraft.chess.items.extendedreach;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.chess.ToroChess;

public class MessageExtendedReachInteract implements IMessage {

  public static final int HIT_TYPE_ENTITY = 0;
  public static final int HIT_TYPE_BLOCK = 1;

  public int hitType;
  public int entityId;
  public BlockPos block;

  public MessageExtendedReachInteract() {

  }

  public MessageExtendedReachInteract(int parEntityId) {
    hitType = HIT_TYPE_ENTITY;
    entityId = parEntityId;
  }

  public MessageExtendedReachInteract(BlockPos pos) {
    hitType = HIT_TYPE_BLOCK;
    block = pos;
  }

  public static void init(int packetId) {
    ToroChess.NETWORK.registerMessage(MessageExtendedReachInteract.Handler.class, MessageExtendedReachInteract.class, packetId, Side.SERVER);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    hitType = buf.readInt();
    entityId = buf.readInt();
    block = BlockPos.fromLong(buf.readLong());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(hitType);
    buf.writeInt(entityId);
    if (block == null) {
      buf.writeLong(0);
    } else {
      buf.writeLong(block.toLong());
    }
  }

  public static class Handler implements IMessageHandler<MessageExtendedReachInteract, IMessage> {

    @Override
    public IMessage onMessage(final MessageExtendedReachInteract message, MessageContext ctx) {
      final EntityPlayerMP payer = ctx.getServerHandler().player;
      payer.getServerWorld().addScheduledTask(new ExtendedReachInteractWorker(payer, message));
      return null;
    }
  }
}