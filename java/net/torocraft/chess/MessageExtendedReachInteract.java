package net.torocraft.chess;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
			final EntityPlayerMP payer = ctx.getServerHandler().playerEntity;
			payer.getServerWorld().addScheduledTask(new Worker(payer, message));
			return null;
		}
	}

	private static class Worker implements Runnable {
		private final EntityPlayerMP player;
		private final MessageExtendedReachInteract message;

		public Worker(EntityPlayerMP player, MessageExtendedReachInteract message) {
			this.player = player;
			this.message = message;
		}

		@Override
		public void run() {
			if (message.hitType == HIT_TYPE_ENTITY) {
				interaceOnEntity(message, player);
			} else if (message.hitType == HIT_TYPE_BLOCK) {
				interaceOnBlock(message, player);
			}
		}

		private void interaceOnBlock(MessageExtendedReachInteract message, EntityPlayerMP player) {
			IExtendedReach extendedReachItem = (IExtendedReach) player.getHeldItemMainhand().getItem();
			double distanceSq = player.getDistanceSq(message.block);
			double reachSq = extendedReachItem.getReach() * extendedReachItem.getReach();

			if (reachSq >= distanceSq) {
				Vec3d vec = player.getPositionVector();
				player.getHeldItemMainhand().onItemUse(player, player.getEntityWorld(), message.block, EnumHand.MAIN_HAND, null, (float) vec.xCoord,
						(float) vec.yCoord, (float) vec.zCoord);
			}
		}

		private void interaceOnEntity(final MessageExtendedReachInteract message, final EntityPlayerMP player) {
			Entity entity = player.world.getEntityByID(message.entityId);

			if (notAnExtendedReachItem(player)) {
				return;
			}

			IExtendedReach extendedReachItem = (IExtendedReach) player.getHeldItemMainhand().getItem();

			double distanceSq = player.getDistanceSqToEntity(entity);
			double reachSq = extendedReachItem.getReach() * extendedReachItem.getReach();

			if (reachSq >= distanceSq) {
				player.interactOn(entity, EnumHand.MAIN_HAND);
			}
		}

		private boolean notAnExtendedReachItem(final EntityPlayerMP player) {
			return player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof IExtendedReach);
		}
	}
}