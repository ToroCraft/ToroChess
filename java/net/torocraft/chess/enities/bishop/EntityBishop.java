package net.torocraft.chess.enities.bishop;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.IChessPiece;

public class EntityBishop extends EntityChessPiece implements IChessPiece {
	public static String NAME = "bishop";

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

}
