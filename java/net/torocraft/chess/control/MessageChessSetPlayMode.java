package net.torocraft.chess.control;


import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.torocraft.chess.ToroChess;
import net.torocraft.chess.control.TileEntityChessControl.PlayMode;
import net.torocraft.chess.engine.GamePieceState.Side;

public class MessageChessSetPlayMode implements IMessage {

	public BlockPos controlBlockPos;
	public Side side;
	public PlayMode mode;

	public static void init(int packetId) {
		ToroChess.NETWORK.registerMessage(MessageChessSetPlayMode.Handler.class, MessageChessSetPlayMode.class, packetId, net.minecraftforge.fml.relauncher.Side.SERVER);
	}

	public MessageChessSetPlayMode() {

	}

	public MessageChessSetPlayMode(BlockPos controlBlockPos, Side side, PlayMode mode) {
		this.controlBlockPos = controlBlockPos;
		this.side = side;
		this.mode = mode;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		controlBlockPos = BlockPos.fromLong(buf.readLong());
		side = Side.values()[buf.readInt()];
		mode = PlayMode.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(controlBlockPos.toLong());
		buf.writeInt(side.ordinal());
		buf.writeInt(mode.ordinal());
	}

	public static class Handler implements IMessageHandler<MessageChessSetPlayMode, IMessage> {
		@Override
		public IMessage onMessage(final MessageChessSetPlayMode message, MessageContext ctx) {
			final EntityPlayerMP payer = ctx.getServerHandler().playerEntity;
			payer.getServerWorld().addScheduledTask(new Worker(payer, message));
			return null;
		}
	}

	private static class Worker implements Runnable {
		private final EntityPlayerMP player;
		private final MessageChessSetPlayMode message;

		public Worker(EntityPlayerMP player, MessageChessSetPlayMode message) {
			this.player = player;
			this.message = message;
		}

		@Override
		public void run() {
			TileEntityChessControl te = (TileEntityChessControl) player.world.getTileEntity(message.controlBlockPos);
			if(Side.WHITE.equals(message.side)){
				te.setWhitePlayMode(message.mode);
			}else{
				te.setBlackPlayMode(message.mode);
			}
		}

	}

}