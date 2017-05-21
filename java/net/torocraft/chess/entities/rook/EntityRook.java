package net.torocraft.chess.entities.rook;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.IChessPiece;

public class EntityRook extends EntityChessPiece implements IChessPiece {

  public static String NAME = ToroChess.MODID + "_rook";

  public EntityRook(World worldIn) {
    super(worldIn);
    setSize(0.6F, 2.9F);
    stepHeight = 1.0F;
  }

  public static void init(int entityId) {
    EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityRook.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
        true);
  }

  public static void registerRenders() {
    RenderingRegistry.registerEntityRenderingHandler(EntityRook.class, new IRenderFactory<EntityRook>() {
      @Override
      public Render<EntityRook> createRenderFor(RenderManager manager) {
        return new RenderRook(manager);
      }
    });
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return null;
  }

  @Override
  public float getEyeHeight() {
    return 2.55F;
  }

  protected SoundEvent getHurtSound() {
    return SoundEvents.ENTITY_ENDERMEN_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_ENDERMEN_DEATH;
  }
}
