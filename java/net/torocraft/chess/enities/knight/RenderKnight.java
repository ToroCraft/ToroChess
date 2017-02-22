package net.torocraft.chess.enities.knight;

import static net.torocraft.chess.engine.GamePieceState.Side.BLACK;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

@SideOnly(Side.CLIENT)
public class RenderKnight extends RenderLiving<EntityKnight> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/pawn_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/pawn_black.png");

	public RenderKnight(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelKnight(), 0.5F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelKnight(0.5F, true);
				this.modelArmor = new ModelKnight(1.0F, true);
			}
		};
		addLayer(layerbipedarmor);
		addLayer(new LayerHeldItem(this));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityKnight entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

}