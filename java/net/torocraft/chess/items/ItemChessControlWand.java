package net.torocraft.chess.items;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;

//TODO: unhiglight all other pieces when a new one is highlighted

//TODO: black/white wand textures

//TODO: white and black wand, only one can work at a time

//TODO: white pieces can only be moved my the white wand

//TODO: wand should only work for this game

//TODO: game controller entity?

//TODO: give pieces a stone texture?

//TODO: highlight location

public class ItemChessControlWand extends Item {

	private static final String NBT_SELECTED_PIECE = "piece";
	private static final String NBT_SELECTED_POS = "pos";
	private static final String NBT_A8_POS = "a8";
	public static ItemChessControlWand INSTANCE;
	public static String NAME = "chess_control_wand";

	public static final double[] TEXTURE_OFFSETS = new double[8];

	static {
		for (int i = 0; i < 8; i++) {
			TEXTURE_OFFSETS[i] = ((double) i) / 8;
		}
	}

	private List<SelectBlock> selectedBlocks = new ArrayList<>();
	private static final ResourceLocation TEXTURE = new ResourceLocation(ToroChess.MODID, "textures/overlay.png");

	public static void init() {
		INSTANCE = new ItemChessControlWand();
		GameRegistry.register(INSTANCE, new ResourceLocation(ToroChess.MODID, NAME));
	}

	public static void registerRenders() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0,
				new ModelResourceLocation(ToroChess.MODID + ":" + NAME, "inventory"));
	}

	public ItemChessControlWand() {
		setUnlocalizedName(NAME);
		setMaxDamage(1);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);

		texureMinX = new double[64];
		texureMaxX = new double[64];
		texureMinY = new double[64];
		texureMaxY = new double[64];
		for (int i = 0; i < 64; i++) {
			texureMinX[i] = (i % 8) / 8.0;
			texureMaxX[i] = (i % 8 + 1) / 8.0;
			texureMinY[i] = (i / 8) / 8.0;
			texureMaxY[i] = (i / 8 + 1) / 8.0;
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {

		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote || !stack.hasTagCompound()) {
			return EnumActionResult.PASS;
		}

		NBTTagCompound c = stack.getTagCompound();

		if (!c.hasKey(NBT_A8_POS) || !c.hasKey(NBT_SELECTED_POS)) {
			return EnumActionResult.PASS;
		}

		BlockPos a8 = BlockPos.fromLong(stack.getTagCompound().getLong(NBT_A8_POS));
		String from = c.getString(NBT_SELECTED_POS);
		String to = CheckerBoardUtil.getPositionName(a8, pos);
		movePiece(world, a8, from, to);

		return EnumActionResult.PASS;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (player.world.isRemote || !(target instanceof EntityChessPiece)) {
			return true;
		}

		EntityChessPiece piece = (EntityChessPiece) target;
		ItemStack stack = player.getHeldItem(hand);
		
		System.out.println(piece.getSide());

		if (target.isPotionActive(MobEffects.GLOWING)) {
			target.removeActivePotionEffect(MobEffects.GLOWING);
			resetWandNbt(stack);
		} else {
			highlightEntity(target);
			setWandNbt(stack, piece);
		}

		System.out.println(stack.getTagCompound() + " " + stack.hasTagCompound());

		return true;
	}

	private static void movePiece(World world, BlockPos a8, String from, String to) {
		EntityChessPiece attacker = getHighlightedPiece(world, from, a8);

		if (attacker == null) {
			return;
		}

		EntityChessPiece victum = getPiece(world, to, a8);
		if (victum == attacker) {
			victum = null;
		}

		attacker.setAttackTarget(victum);
		attacker.setChessPosition(to);
	}

	private static EntityChessPiece getHighlightedPiece(World world, String piecePos, BlockPos a8) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.getPosition(a8, piecePos)).expand(80, 20, 80), HIGHLIGHTED_PIECED);

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static EntityChessPiece getPiece(World world, String piecePos, BlockPos a8) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.getPosition(a8, piecePos)).expand(80, 20, 80), new ChessPieceAtPredicate(piecePos));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static void setWandNbt(ItemStack wand, EntityChessPiece target) {
		NBTTagCompound c = wand.getTagCompound();
		if (c == null) {
			c = new NBTTagCompound();
		}
		c.setString(NBT_SELECTED_POS, ((EntityChessPiece) target).getChessPosition());
		c.setLong(NBT_A8_POS, ((EntityChessPiece) target).getA8().toLong());
		c.setString(NBT_SELECTED_PIECE, target.getName());
		wand.setTagCompound(c);
	}

	private static void resetWandNbt(ItemStack wand) {
		if (!wand.hasTagCompound()) {
			return;
		}
		NBTTagCompound c = wand.getTagCompound();
		c.removeTag(NBT_SELECTED_PIECE);
		c.removeTag(NBT_SELECTED_POS);
		c.removeTag(NBT_A8_POS);
	}

	private static void highlightEntity(EntityLivingBase target) {
		removeAllHighlights(target.world, target.getPosition());
		PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false);
		target.addPotionEffect(potioneffect);
	}

	private static void removeAllHighlights(World world, BlockPos pos) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				HIGHLIGHTED_PIECED);
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	private static Predicate<EntityChessPiece> HIGHLIGHTED_PIECED = new Predicate<EntityChessPiece>() {
		@Override
		public boolean apply(EntityChessPiece e) {
			return e.isPotionActive(MobEffects.GLOWING);
		}
	};

	private static class ChessPieceAtPredicate implements Predicate<EntityChessPiece> {
		private final String chessPosition;

		public ChessPieceAtPredicate(String chessPosition) {
			this.chessPosition = chessPosition;
		}

		@Override
		public boolean apply(EntityChessPiece e) {
			return e.getChessPosition().equals(chessPosition);
		}
	};

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		selectedBlocks.clear();
		
		if (player == null) {
			return;
		}

		ItemStack itemStack = player.getHeldItemMainhand();

		if (itemStack == null || itemStack.getItem() != INSTANCE || !itemStack.hasTagCompound()) {
			return;
		}

		BlockPos a8 = BlockPos.fromLong(itemStack.getTagCompound().getLong(NBT_A8_POS));

		if (a8 == null) {
			return;
		}

		RayTraceResult r = player.rayTrace(50, 1);
		if (r.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
			setSelectedBlock(a8, r);
		}
		
		if(selectedBlocks.size() < 1){
			return;
		}

		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		render(x, y, z);

	}

	private void setSelectedBlock(BlockPos a8, RayTraceResult r) {
		SelectBlock b = new SelectBlock();
		BlockPos offset = r.getBlockPos().subtract(a8);
		if(offset.getX() > 7 || offset.getX() < 0 || offset.getZ() > 7 || offset.getZ() < 0 || offset.getY() != 0){
			return;
		}
		b.u = 7 - offset.getX();
		b.v = offset.getZ();
		b.pos = r.getBlockPos();
		selectedBlocks.add(b);
	}

	private double[] texureMinX, texureMaxX;
	private double[] texureMinY, texureMaxY;

	public void render(double x, double y, double z) {
		if(selectedBlocks.size() < 1){
			return;
		}
		TextureManager tm = Minecraft.getMinecraft().renderEngine;
		tm.bindTexture(TEXTURE);
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.setTranslation(-x, -y, -z);
		for (SelectBlock u : selectedBlocks) {
			renderVectors(vb, u);
		}
		vb.setTranslation(0, 0, 0);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderVectors(VertexBuffer vb, SelectBlock info) {
		double x = info.pos.getX();
		double y = info.pos.getY() + 1.001;
		double z = info.pos.getZ();

		double u = TEXTURE_OFFSETS[info.u];
		double v = TEXTURE_OFFSETS[info.v];

		vb.pos(x, y, z);
		vb.tex(u, v);
		vb.color(255, 255, 255, 255);
		vb.endVertex();

		vb.pos(x, y, z + 1);
		vb.tex(u, v + 0.125);
		vb.color(255, 255, 255, 255);
		vb.endVertex();

		vb.pos(x + 1, y, z + 1);
		vb.tex(u + 0.125, v + 0.125);
		vb.color(255, 255, 255, 255);
		vb.endVertex();

		vb.pos(x + 1, y, z);
		vb.tex(u + 0.125, v);
		vb.color(255, 255, 255, 255);
		vb.endVertex();
	}

	public static class SelectBlock {
		public int u, v;
		public BlockPos pos;
	}
}
