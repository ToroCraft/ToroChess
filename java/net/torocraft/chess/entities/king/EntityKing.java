package net.torocraft.chess.entities.king;

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

public class EntityKing extends EntityChessPiece implements IChessPiece {
	public static String NAME = "king";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityKing.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityKing.class, new IRenderFactory<EntityKing>() {
			@Override
			public Render<EntityKing> createRenderFor(RenderManager manager) {
				return new RenderKing(manager);
			}
		});
	}

	public EntityKing(World worldIn) {
		super(worldIn);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

}
