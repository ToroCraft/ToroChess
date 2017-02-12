package net.torocraft.chess.enities.bishop;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBishop extends RenderLiving<EntityBishop> {
	
	private static final ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");
	
	public RenderBishop(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBishop(0.0F), 0.5F);
		
	}
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be
	 * called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityBishop entity) {
		return witchTextures;
	}

}