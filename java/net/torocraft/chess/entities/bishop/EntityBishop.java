package net.torocraft.chess.entities.bishop;

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

public class EntityBishop extends EntityChessPiece implements IChessPiece {
	public static String NAME = ToroChess.MODID + "_bishop";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityBishop.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true, 0xFFFFFF, 0x000000);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBishop.class, new IRenderFactory<EntityBishop>() {
			@Override
			public Render<EntityBishop> createRenderFor(RenderManager manager) {
				return new RenderBishop(manager);
			}
		});
	}

	public EntityBishop(World worldIn) {
		super(worldIn);
		setSize(0.6F, 1.95F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	public float getEyeHeight() {
		return 1.62F;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITCH_DEATH;
	}

}
