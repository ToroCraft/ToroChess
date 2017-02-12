package net.torocraft.chess.enities.rook;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRook extends RenderLiving<EntityRook> {

	private static final ResourceLocation endermanTextures = new ResourceLocation(
			"textures/entity/enderman/enderman.png");

	public RenderRook(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelRook(0.0F), 0.5F);

	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityRook entity) {
		return endermanTextures;
	}

}