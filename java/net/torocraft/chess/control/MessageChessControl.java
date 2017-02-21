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

	public BlockPos controlBlockPos;

	public static void init(int packetId) {
		ToroChess.NETWORK.registerMessage(MessageChessControl.Handler.class, MessageChessControl.class, packetId, Side.SERVER);
	}

	public MessageChessControl() {

	}

	public MessageChessControl(BlockPos controlBlockPos) {
		this.controlBlockPos = controlBlockPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		controlBlockPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (controlBlockPos == null) {
			buf.writeLong(0);
		} else {
			buf.writeLong(controlBlockPos.toLong());
		}
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
			// TODO Auto-generated method stub

			System.out.println("chess control message worker for " + message.controlBlockPos);

			((TileEntityChessControl) player.world.getTileEntity(message.controlBlockPos)).resetBoard();

		}

	}

}