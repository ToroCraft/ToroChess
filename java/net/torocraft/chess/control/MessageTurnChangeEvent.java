package net.torocraft.chess.control;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.items.ItemChessControlWand;

public class MessageTurnChangeEvent implements IMessage {

	public Side currentSide;
	public UUID gameId;

	public static void init(int packetId) {
		ToroChess.NETWORK.registerMessage(MessageTurnChangeEvent.Handler.class, MessageTurnChangeEvent.class, packetId,
				net.minecraftforge.fml.relauncher.Side.CLIENT);
	}

	public MessageTurnChangeEvent() {

	}

	public MessageTurnChangeEvent(Side currentSide, UUID gameId) {
		this.currentSide = currentSide;
		this.gameId = gameId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			currentSide = Side.values()[buf.readByte()];
			gameId = uuidFromBytes(buf);
		} catch (Exception e) {
			currentSide = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (currentSide == null) {
			throw new NullPointerException("currentSide is null");
		}
		if (gameId == null) {
			throw new NullPointerException("gameId is null");
		}
		buf.writeByte(currentSide.ordinal());
		uuidToBytes(buf, gameId);
	}

	public static void uuidToBytes(ByteBuf buf, UUID uuid) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID uuidFromBytes(ByteBuf buf) {
		long msb = buf.readLong();
		long lsb = buf.readLong();
		return new UUID(msb, lsb);
	}

	public static class Handler implements IMessageHandler<MessageTurnChangeEvent, IMessage> {
		@Override
		public IMessage onMessage(final MessageTurnChangeEvent message, MessageContext ctx) {

			if (message.currentSide == null) {
				return null;
			}

			IThreadListener mainThread = Minecraft.getMinecraft();
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					ItemStack wand = player.getHeldItemMainhand();
					if (wand == null || wand.isEmpty() || wand.getItem() != ItemChessControlWand.INSTANCE) {
						return;
					}

					UUID wandGameId = ItemChessControlWand.getGameId(wand);

					if (message.gameId.equals(wandGameId)) {
						CheckerBoardOverlay.INSTANCE.setValidMoves(null);
					}
				}
			});

			return null;
		}
	}

}