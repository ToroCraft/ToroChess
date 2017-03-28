package net.torocraft.chess.entities.king;

import static net.torocraft.chess.engine.GamePieceState.Side.BLACK;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;

@SideOnly(Side.CLIENT)
public class RenderKing extends RenderLiving<EntityKing> {

	private static final ResourceLocation WHITE_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/king_white.png");
	private static final ResourceLocation BLACK_TEXTURES = new ResourceLocation(ToroChess.MODID, "textures/entity/king_black.png");

	public RenderKing(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelKing(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityKing entity) {
		if (BLACK.equals(entity.getSide())) {
			return BLACK_TEXTURES;
		} else {
			return WHITE_TEXTURES;
		}
	}

	protected void applyRotations(EntityKing entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
		if (entityLiving.isInCheck()) {
			p_77043_3_ += (float) (Math.cos((double) entityLiving.ticksExisted * 3.25D) * Math.PI * 0.5D);
		}
		super.applyRotations(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
	}
}