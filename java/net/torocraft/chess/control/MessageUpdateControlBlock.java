package net.torocraft.chess.control;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.items.ItemChessControlWand;

public class MessageUpdateControlBlock implements IMessage {

  public BlockPos controlBlockPos;
  public NBTTagCompound data;

  public MessageUpdateControlBlock() {

  }

  public MessageUpdateControlBlock(BlockPos controlBlockPos, NBTTagCompound data) {
    this.controlBlockPos = controlBlockPos;
    this.data = data;
  }

  public static void init(int packetId) {
    ToroChess.NETWORK.registerMessage(MessageUpdateControlBlock.Handler.class, MessageUpdateControlBlock.class, packetId, Side.CLIENT);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    controlBlockPos = BlockPos.fromLong(buf.readLong());
    data = ByteBufUtils.readTag(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(controlBlockPos.toLong());
    ByteBufUtils.writeTag(buf, data);
  }

  public static class Handler implements IMessageHandler<MessageUpdateControlBlock, IMessage> {

    @Override
    public IMessage onMessage(final MessageUpdateControlBlock message, MessageContext ctx) {

      if (message.controlBlockPos == null) {
        return null;
      }

      final IThreadListener mainThread = Minecraft.getMinecraft();
      final EntityPlayerSP player = Minecraft.getMinecraft().player;

      mainThread.addScheduledTask(new Runnable() {
        @Override
        public void run() {
          TileEntityChessControl control = ItemChessControlWand.getChessControlAt(player.world, message.controlBlockPos);
          if (control == null) {
            System.out.println("control block not found");
            return;
          }
          control.readFromNBT(message.data);
        }
      });

      return null;
    }
  }

}