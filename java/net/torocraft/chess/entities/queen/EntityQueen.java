package net.torocraft.chess.entities.queen;

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

public class EntityQueen extends EntityChessPiece implements IChessPiece {
	public static String NAME = "queen";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityQueen.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityQueen.class, new IRenderFactory<EntityQueen>() {
			@Override
			public Render<EntityQueen> createRenderFor(RenderManager manager) {
				return new RenderQueen(manager);
			}
		});
	}

	public EntityQueen(World worldIn) {
		super(worldIn);
		this.setSize(0.7F, 2.4F);
	}

	public float getEyeHeight() {
		return 2.1F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
	}

}
