package net.torocraft.chess.enities.queen;

import static net.torocraft.chess.engine.GamePieceState.Side.BLACK;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

@SideOnly(Side.CLIENT)
public class RenderQueen extends RenderBiped<EntityQueen> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/queen_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/queen_black.png");

	public RenderQueen(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelQueen(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityQueen entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

	@Override
	protected void preRenderCallback(EntityQueen entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(1.2F, 1.2F, 1.2F);
	}
}