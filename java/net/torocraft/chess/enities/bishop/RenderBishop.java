package net.torocraft.chess.enities.bishop;

import static net.torocraft.chess.engine.ChessPieceState.Side.BLACK;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

@SideOnly(Side.CLIENT)
public class RenderBishop extends RenderLiving<EntityBishop> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/bishop_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/bishop_black.png");

	public RenderBishop(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBishop(0.0F), 0.5F);
	}

	protected ResourceLocation getEntityTexture(EntityBishop entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

}