package net.torocraft.chess.enities.king;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderKing extends RenderLiving<EntityKing> {
	
	private static final ResourceLocation witherSkeletonTextures = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	
	public RenderKing(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelKing(), 0.5F);
		
	}
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be
	 * called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityKing entity) {
		return witherSkeletonTextures;
	}

}