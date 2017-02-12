package net.torocraft.chess.enities.pawn;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPawn extends RenderLiving<EntityPawn> {

	private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");

	public RenderPawn(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelPawn(), 0.5F);

	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityPawn entity) {
		return zombieTextures;
	}

}