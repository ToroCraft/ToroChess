package net.torocraft.chess.enities.pawn;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

import static net.torocraft.chess.engine.ChessPieceState.Side.BLACK;

@SideOnly(Side.CLIENT)
public class RenderPawn extends RenderLiving<EntityPawn> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/pawn_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/pawn_black.png");

	public RenderPawn(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelPawn(), 0.5F);
	}

	protected ResourceLocation getEntityTexture(EntityPawn entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

}