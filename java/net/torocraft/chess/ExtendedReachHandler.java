package net.torocraft.chess;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExtendedReachHandler {
	// TODO move this
	public static SimpleNetworkWrapper INSTANCE;

	public static void init() {
		// TODO move network wrapper to a shared location
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ToroChess.MODID);

		int packetId = 0;
		INSTANCE.registerMessage(MessageExtendedReachInteract.Handler.class, MessageExtendedReachInteract.class, packetId++, Side.SERVER);

		MinecraftForge.EVENT_BUS.register(new ExtendedReachHandler());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(MouseEvent event) {
		if (event.getButton() != 1) {
			return;
		}
		if (!event.isButtonstate()) {
			return;
		}

		EntityPlayer player = Minecraft.getMinecraft().player;

		if (player == null) {
			return;
		}
		
		ItemStack stack = player.getHeldItemMainhand();

		if (stack == null || !(stack.getItem() instanceof IExtendedReach)) {
			return;
		}

		IExtendedReach extendedReachItem = (IExtendedReach) stack.getItem();
		RayTraceResult raytrace = getMouseOverExtended(extendedReachItem.getReach());

		if (raytrace == null) {
			return;
		}

		if (raytrace.entityHit != null) {
			INSTANCE.sendToServer(new MessageExtendedReachInteract(raytrace.entityHit.getEntityId()));
		} else if (RayTraceResult.Type.BLOCK.equals(raytrace.typeOfHit)) {
			INSTANCE.sendToServer(new MessageExtendedReachInteract(raytrace.getBlockPos()));
		}
		

	}

	public static RayTraceResult getMouseOverExtended(float dist) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(theRenderViewEntity.posX - 0.5D, theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D, theRenderViewEntity.posX + 0.5D, theRenderViewEntity.posY + 1.5D, theRenderViewEntity.posZ + 0.5D);
		RayTraceResult returnMOP = null;
		if (mc.world != null) {
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace(var2, 0);
			double calcdist = var2;
			Vec3d pos = theRenderViewEntity.getPositionEyes(0);
			var2 = calcdist;
			if (returnMOP != null) {
				calcdist = returnMOP.hitVec.distanceTo(pos);
			}

			Vec3d lookvec = theRenderViewEntity.getLook(0);
			Vec3d var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2);
			Entity pointedEntity = null;
			float var9 = 1.0F;

			List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity,
					theViewBoundingBox.addCoord(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2).expand(var9, var9, var9));
			double d = calcdist;

			for (Entity entity : list) {
				if (entity.canBeCollidedWith()) {
					float bordersize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - entity.width / 2, entity.posY, entity.posZ - entity.width / 2,
							entity.posX + entity.width / 2, entity.posY + entity.height, entity.posZ + entity.width / 2);
					aabb.expand(bordersize, bordersize, bordersize);
					RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);

					if (aabb.isVecInside(pos)) {
						if (0.0D < d || d == 0.0D) {
							pointedEntity = entity;
							d = 0.0D;
						}
					} else if (mop0 != null) {
						double d1 = pos.distanceTo(mop0.hitVec);

						if (d1 < d || d == 0.0D) {
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
				returnMOP = new RayTraceResult(pointedEntity);
			}
		}
		return returnMOP;
	}

}
