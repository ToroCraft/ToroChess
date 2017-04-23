package net.torocraft.chess.control;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Side;

public class MessageChessChat implements IMessage {

	private String message;
	private Side side;
	private Position from;
	private Position to;
	private String name;

	public static void init(int packetId) {
		ToroChess.NETWORK.registerMessage(MessageChessChat.Handler.class, MessageChessChat.class, packetId,
				net.minecraftforge.fml.relauncher.Side.CLIENT);
	}

	public MessageChessChat() {

	}

	public MessageChessChat(String message, Side side, String name, Position from, Position to) {
		this.message = message;
		this.side = side;
		this.from = from;
		this.to = to;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, message);
	}

	public static class Handler implements IMessageHandler<MessageChessChat, IMessage> {
		@Override
		public IMessage onMessage(final MessageChessChat message, MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			EntityPlayer player = Minecraft.getMinecraft().player;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					String translated = I18n.format(message.message, s(message.name), s(message.side), s(message.to), s(message.from));
					player.sendMessage(new TextComponentString(translated));
				}

				private Object s(String name) {
					if(name == null){
						return "";
					}
					return name;
				}
		

				private Object s(Side side) {
					if(side == null){
						return "";
					}
					return side.toString();
				}

				private Object s(Position p) {
					if(p == null){
						return "";
					}
					return p.toString();
				}
			});
			return null;
		}
	}

}