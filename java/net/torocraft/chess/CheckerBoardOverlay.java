package net.torocraft.chess;

import java.util.ArrayList;
import java.util.Iterator;
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
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Rank;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.items.ItemChessControlWand;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class CheckerBoardOverlay {

	public static CheckerBoardOverlay INSTANCE;

	private static final double T = 0.125f;
	private static final double[] TEXTURE_OFFSETS = new double[8];
	private static final ResourceLocation LOCATIONS_TEXTURE = new ResourceLocation(ToroChess.MODID, "textures/overlay.png");
	private static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(ToroChess.MODID, "textures/icons.png");

	private final List<Overlay> overlays = new ArrayList<>();
	private List<Position> moves;

	public static void init() {
		INSTANCE = new CheckerBoardOverlay();
		for (int i = 0; i < 8; i++) {
			TEXTURE_OFFSETS[i] = ((double) i) / 8;
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	public void setValidMoves(List<Position> moves) {
		this.moves = moves;
	}

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		removeOldOverlays();
		addBlockUnderCrosshairs(event);
	}

	private void removeOldOverlays() {
		if (overlays.size() < 1) {
			return;
		}
		for (Iterator<Overlay> iter = overlays.iterator(); iter.hasNext();) {
			Overlay overlay = iter.next();
			overlay.life--;
			if (overlay.life < 0) {
				iter.remove();
			}
		}
	}

	private void addBlockUnderCrosshairs(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

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
			addCursorOverlay(a8, r);
		}

		if (overlays.size() < 1) {
			return;
		}

		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		render(x, y, z, side);
	}

	private void addCursorOverlay(BlockPos a8, RayTraceResult r) {
		Overlay overlay = new Overlay();
		BlockPos offset = r.getBlockPos().subtract(a8);
		if (offset.getX() > 7 || offset.getX() < 0 || offset.getZ() > 7 || offset.getZ() < 0 || offset.getY() != 0) {
			return;
		}

		if (moves != null && moves.size() > 0) {
			Position p = new Position(File.values()[7 - offset.getX()], Rank.values()[offset.getZ()]);
			for (Position move : moves) {
				if (move.file.equals(p.file) && move.rank.equals(p.rank)) {
					overlay.valid = true;
					break;
				}
			}
		}

		overlay.u = 7 - offset.getX();
		overlay.v = offset.getZ();
		overlay.pos = r.getBlockPos();
		overlays.add(overlay);
	}

	public void render(double x, double y, double z, Side side) {
		if (overlays.size() < 1) {
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
		for (Overlay select : overlays) {
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
		for (Overlay select : overlays) {
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

	public static class Overlay {
		public int u, v;
		public BlockPos pos;
		public boolean valid;
		public int life;
	}
}
