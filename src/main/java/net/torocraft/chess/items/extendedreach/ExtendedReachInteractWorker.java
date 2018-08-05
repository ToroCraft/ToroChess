package net.torocraft.chess.items.extendedreach;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class ExtendedReachInteractWorker implements Runnable {

  private final EntityPlayerMP player;
  private final MessageExtendedReachInteract message;

  public ExtendedReachInteractWorker(EntityPlayerMP player, MessageExtendedReachInteract message) {
    this.player = player;
    this.message = message;
  }

  @Override
  public void run() {
    if (message.hitType == MessageExtendedReachInteract.HIT_TYPE_ENTITY) {
      interaceOnEntity(message, player);
    } else if (message.hitType == MessageExtendedReachInteract.HIT_TYPE_BLOCK) {
      interactOnBlock(message, player);
    }
  }

  private void interactOnBlock(MessageExtendedReachInteract message, EntityPlayerMP player) {
    IExtendedReach extendedReachItem = (IExtendedReach) player.getHeldItemMainhand().getItem();
    double distanceSq = player.getDistanceSq(message.block);
    double reachSq = extendedReachItem.getReach() * extendedReachItem.getReach();

    if (reachSq >= distanceSq) {
      Vec3d vec = player.getPositionVector();

      extendedReachItem.onItemUseExtended(player, player.getEntityWorld(), message.block, EnumHand.MAIN_HAND, null, (float) vec.x,
          (float) vec.y, (float) vec.z);

      player.swingArm(EnumHand.MAIN_HAND);
    }
  }

  private void interaceOnEntity(final MessageExtendedReachInteract message, final EntityPlayerMP player) {
    Entity entity = player.world.getEntityByID(message.entityId);

    if (!(entity instanceof EntityLivingBase)) {
      return;
    }

    if (notAnExtendedReachItem(player)) {
      return;
    }

    IExtendedReach extendedReachItem = (IExtendedReach) player.getHeldItemMainhand().getItem();

    double distanceSq = player.getDistanceSq(entity);
    double reachSq = extendedReachItem.getReach() * extendedReachItem.getReach();

    if (reachSq >= distanceSq) {
      extendedReachItem.itemInteractionForEntityExtended(player.getHeldItemMainhand(), player, (EntityLivingBase) entity, EnumHand.MAIN_HAND);
      player.swingArm(EnumHand.MAIN_HAND);
    }
  }

  private boolean notAnExtendedReachItem(final EntityPlayerMP player) {
    return player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof IExtendedReach);
  }
}