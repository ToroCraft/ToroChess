package net.torocraft.chess.enities.rook;

import static net.torocraft.chess.enities.IChessPiece.Side.BLACK;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

@SideOnly(Side.CLIENT)
public class RenderRook extends RenderLiving<EntityRook> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/rook_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/rook_black.png");

	public RenderRook(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelRook(0.0F), 0.5F);
	}

	protected ResourceLocation getEntityTexture(EntityRook entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

}