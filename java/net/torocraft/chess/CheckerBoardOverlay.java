package net.torocraft.chess;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.items.ItemChessControlWand;

public class CheckerBoardOverlay {
	public static final double[] TEXTURE_OFFSETS = new double[8];

	/**
	 * texture unit offset
	 */
	private static double T = 0.125f;

	private List<SelectBlock> selectedBlocks = new ArrayList<>();
	private static final ResourceLocation LOCATIONS_TEXTURE = new ResourceLocation(ToroChess.MODID, "textures/overlay.png");
	private static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(ToroChess.MODID, "textures/icons.png");

	static {
		for (int i = 0; i < 8; i++) {
			TEXTURE_OFFSETS[i] = ((double) i) / 8;
		}
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new CheckerBoardOverlay());
	}

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		selectedBlocks.clear();

		if (player == null) {
			return;
		}

		ItemStack wand = player.getHeldItemMainhand();

		if (wand == null || wand.getItem() != ItemChessControlWand.INSTANCE || !wand.hasTagCompound()) {
			return;
		}

		BlockPos a8 = ItemChessControlWand.getA8(wand);
		Side side = ItemChessControlWand.getSide(wand);

		if (a8 == null) {
			return;
		}

		RayTraceResult r = player.rayTrace(50, 1);
		if (r.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
			setSelectedBlock(a8, r);
		}

		if (selectedBlocks.size() < 1) {
			return;
		}

		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		render(x, y, z, side);

	}

	private void setSelectedBlock(BlockPos a8, RayTraceResult r) {
		SelectBlock b = new SelectBlock();
		BlockPos offset = r.getBlockPos().subtract(a8);
		if (offset.getX() > 7 || offset.getX() < 0 || offset.getZ() > 7 || offset.getZ() < 0 || offset.getY() != 0) {
			return;
		}

		b.valid = offset.getX() == 0;

		b.u = 7 - offset.getX();
		b.v = offset.getZ();
		b.pos = r.getBlockPos();
		selectedBlocks.add(b);
	}

	public void render(double x, double y, double z, Side side) {
		if (selectedBlocks.size() < 1) {
			return;
		}
		TextureManager tm = Minecraft.getMinecraft().renderEngine;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		drawLocationVectors(x, y, z, side, tm);
		drawIconVectors(x, y, z, side, tm);

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void drawLocationVectors(double x, double y, double z, Side side, TextureManager tm) {
		tm.bindTexture(LOCATIONS_TEXTURE);
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.setTranslation(-x, -y, -z);
		for (SelectBlock select : selectedBlocks) {
			renderVectors(vb, select.pos, TEXTURE_OFFSETS[select.u], TEXTURE_OFFSETS[select.v], side, 1.002);
		}
		vb.setTranslation(0, 0, 0);
		Tessellator.getInstance().draw();
	}

	private void drawIconVectors(double x, double y, double z, Side side, TextureManager tm) {
		VertexBuffer vb;
		tm.bindTexture(ICONS_TEXTURE);
		vb = Tessellator.getInstance().getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.setTranslation(-x, -y, -z);
		for (SelectBlock select : selectedBlocks) {
			if (select.valid) {
				renderVectors(vb, select.pos, TEXTURE_OFFSETS[0], TEXTURE_OFFSETS[0], side, 1.001);
			}
		}
		vb.setTranslation(0, 0, 0);
		Tessellator.getInstance().draw();
	}

	private void renderVectors(VertexBuffer vb, BlockPos pos, double u, double v, Side side, double yOffset) {
		double x = pos.getX();
		double y = pos.getY() + yOffset;
		double z = pos.getZ();

		if (Side.WHITE.equals(side)) {
			vector(vb, x, y, z, u, v, 0, 0, T, T);
			vector(vb, x, y, z, u, v, 0, 1, T, 0);
			vector(vb, x, y, z, u, v, 1, 1, 0, 0);
			vector(vb, x, y, z, u, v, 1, 0, 0, T);
		} else {
			vector(vb, x, y, z, u, v, 0, 0, 0, 0);
			vector(vb, x, y, z, u, v, 0, 1, 0, T);
			vector(vb, x, y, z, u, v, 1, 1, T, T);
			vector(vb, x, y, z, u, v, 1, 0, T, 0);
		}
	}

	private void vector(VertexBuffer vb, double x, double y, double z, double u, double v, int oX, int oZ, double oU, double oV) {
		vb.pos(x + oX, y, z + oZ);
		vb.tex(u + oU, v + oV);
		vb.color(255, 255, 255, 255);
		vb.endVertex();
	}

	public static class SelectBlock {
		public int u, v;
		public BlockPos pos;
		public boolean valid;
	}
}
