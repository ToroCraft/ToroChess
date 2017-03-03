package net.torocraft.chess.entities.pawn;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.IChessPiece;

public class EntityPawn extends EntityChessPiece implements IChessPiece {
	public static String NAME = "pawn";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityPawn.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityPawn.class, new IRenderFactory<EntityPawn>() {
			@Override
			public Render<EntityPawn> createRenderFor(RenderManager manager) {
				return new RenderPawn(manager);
			}
		});
	}

	public EntityPawn(World worldIn) {
		super(worldIn);
		super.setSize(0.4F, 1.1F);

	}

	@Override
	public boolean isChild() {
		return true;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	public float getEyeHeight() {
		float f = 1.74F;

		if (this.isChild()) {
			f = (float) ((double) f - 0.81D);
		}

		return f;
	}

}
