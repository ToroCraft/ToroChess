package net.torocraft.chess.items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
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
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.IChessPiece.Side;
import net.torocraft.chess.gen.CheckerBoardUtil;

//TODO: white and black wand, only one can work at a time (current turn check)

//TODO: game controller entity, something to hold the game state?

//TODO: highlight location

//long range item clicking

//add banners, limit stairs to 5 levels

//wand should remember A8 and gameId

//craftable control blocks with blocks that will be placed (so the square colors can be changed)

//api MC(move, clear, set) CHESS(isValid, getState)

//left clicking board with wand opens game settings GUI, or right clicking the game control block

//king arms are missing

//add a crown texture for the king

//pieces can't travel to some squares, white can't reach h1, g1, h2, g2, f2 for example

// valid move overlay indicators

//move timers

//wands are placed in the wrong chest

//no head shake AI when player tries an incorrect move

//add sounds to select/deselection and move targeting and incorrectly selections

//place instruction books in the chests

public class ItemChessControlWand extends Item {

	public static final String NBT_SELECTED_POS = "chesspos";
	public static final String NBT_SIDE = "chessside";
	public static final String NBT_A8_POS = "chessa8";
	public static final String NBT_GAME_ID = "chessgameid";
	public static final String NAME = "chess_control_wand";
	public static final ModelResourceLocation MODEL_BLACK = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_black", "inventory");
	public static final ModelResourceLocation MODEL_WHITE = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_white", "inventory");
	public static final double[] TEXTURE_OFFSETS = new double[8];

	/**
	 * texture unit offset
	 */
	private static double T = 0.125f;

	public static ItemChessControlWand INSTANCE;

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
		ModelLoader.setCustomMeshDefinition(INSTANCE, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (Side.WHITE.equals(getSide(stack))) {
					return MODEL_WHITE;
				} else {
					return MODEL_BLACK;
				}
			}
		});
		ModelLoader.registerItemVariants(INSTANCE, new ModelResourceLocation[] { MODEL_WHITE, MODEL_BLACK });
		MinecraftForge.EVENT_BUS.register(ItemChessControlWand.INSTANCE);
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

		ItemStack wand = player.getHeldItem(hand);

		if (world.isRemote || !wand.hasTagCompound()) {
			return EnumActionResult.PASS;
		}

		NBTTagCompound c = wand.getTagCompound();

		if (!c.hasKey(NBT_A8_POS) || !c.hasKey(NBT_SELECTED_POS) || !c.hasKey(NBT_SIDE)) {
			return EnumActionResult.PASS;
		}

		BlockPos a8 = BlockPos.fromLong(wand.getTagCompound().getLong(NBT_A8_POS));
		String from = c.getString(NBT_SELECTED_POS);
		String to = CheckerBoardUtil.getPositionName(a8, pos);
		Side side = castSide(c.getBoolean(NBT_SIDE));
		UUID gameId = c.getUniqueId(NBT_GAME_ID);
		
		if(gameId == null){
			return EnumActionResult.PASS;
		}

		movePiece(world, wand, gameId, a8, side, from, to);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (player.world.isRemote || !(target instanceof EntityChessPiece)) {
			return false;
		}

		EntityChessPiece piece = (EntityChessPiece) target;
		ItemStack wand = player.getHeldItem(hand);

		NBTTagCompound c = wand.getTagCompound();
		if (!c.hasKey(NBT_A8_POS) || !c.hasKey(NBT_SIDE)) {
			return false;
		}
		
		UUID gameId = c.getUniqueId(NBT_GAME_ID);
		
		if (gameId == null || !gameId.equals(piece.getGameId())) {
			return false;
		}

		Side side = castSide(c.getBoolean(NBT_SIDE));

		if (!side.equals(piece.getSide())) {
			return handleClickOnEnemy(player.world, wand, piece);
		} else {
			return handleClickOnFriend(wand, piece);
		}

	}

	private boolean handleClickOnEnemy(World world, ItemStack wand, EntityChessPiece enemyPiece) {
		String from = wand.getTagCompound().getString(NBT_SELECTED_POS);
		if (from == null) {
			return false;
		}

		BlockPos a8 = BlockPos.fromLong(wand.getTagCompound().getLong(NBT_A8_POS));
		String to = enemyPiece.getChessPosition();
		Side side = castSide(wand.getTagCompound().getBoolean(NBT_SIDE));
		UUID gameId = wand.getTagCompound().getUniqueId(NBT_GAME_ID);
		
		if(gameId == null){
			return false;
		}

		movePiece(world, wand, gameId, a8, side, from, to);
		return true;
	}

	private boolean handleClickOnFriend(ItemStack stack, EntityChessPiece friendlyPiece) {
		if (friendlyPiece.isPotionActive(MobEffects.GLOWING)) {
			friendlyPiece.removeActivePotionEffect(MobEffects.GLOWING);
			clearSelectedNbt(stack);

		} else {
			highlightEntity(friendlyPiece);
			setSelectedNbt(stack, friendlyPiece);

		}
		return true;
	}

	private static void movePiece(World world, ItemStack stack, UUID gameId, BlockPos a8, Side side, String from, String to) {
		EntityChessPiece attacker = getHighlightedPiece(world, from, a8, gameId);

		if (attacker == null) {
			return;
		}

		EntityChessPiece victum = getPiece(world, to, a8, gameId);
		if (victum != null && victum.getSide().equals(side)) {
			victum = null;
			return;
		}

		attacker.removeActivePotionEffect(MobEffects.GLOWING);
		clearSelectedNbt(stack);

		attacker.setAttackTarget(victum);
		attacker.setChessPosition(to);
	}

	private static EntityChessPiece getHighlightedPiece(World world, String piecePos, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.getPosition(a8, piecePos)).expand(80, 20, 80), new HighlightedChessPiecePredicate(gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static EntityChessPiece getPiece(World world, String piecePos, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.getPosition(a8, piecePos)).expand(80, 20, 80), new ChessPieceAtPredicate(piecePos, gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	private static void setSelectedNbt(ItemStack wand, EntityChessPiece target) {
		NBTTagCompound c = wand.getTagCompound();
		c.setString(NBT_SELECTED_POS, ((EntityChessPiece) target).getChessPosition());
		wand.setTagCompound(c);
	}

	private static void clearSelectedNbt(ItemStack wand) {
		NBTTagCompound c = wand.getTagCompound();
		c.removeTag(NBT_SELECTED_POS);
	}

	private static void highlightEntity(EntityChessPiece target) {
		removeAllHighlights(target.world, target.getPosition(), target.getGameId());
		PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false);
		target.addPotionEffect(potioneffect);
	}

	private static void removeAllHighlights(World world, BlockPos pos, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new HighlightedChessPiecePredicate(gameId));
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	private static class HighlightedChessPiecePredicate implements Predicate<EntityChessPiece> {

		private final UUID gameId;

		public HighlightedChessPiecePredicate(UUID gameId) {
			this.gameId = gameId;
		}

		@Override
		public boolean apply(EntityChessPiece e) {
			if (e.getGameId() == null) {
				return false;
			}
			return e.getGameId().equals(gameId) && e.isPotionActive(MobEffects.GLOWING);
		}
	};

	private static class ChessPieceAtPredicate implements Predicate<EntityChessPiece> {
		private final String chessPosition;
		private final UUID gameId;

		public ChessPieceAtPredicate(String chessPosition, UUID gameId) {
			this.chessPosition = chessPosition;
			this.gameId = gameId;
		}

		@Override
		public boolean apply(EntityChessPiece e) {
			if (e.getChessPosition() == null || e.getGameId() == null) {
				return false;
			}
			return e.getChessPosition().equals(chessPosition) && e.getGameId().equals(gameId);
		}
	};

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		selectedBlocks.clear();

		if (player == null) {
			return;
		}

		ItemStack wand = player.getHeldItemMainhand();

		if (wand == null || wand.getItem() != INSTANCE || !wand.hasTagCompound()) {
			return;
		}

		BlockPos a8 = BlockPos.fromLong(wand.getTagCompound().getLong(NBT_A8_POS));
		Side side = getSide(wand);

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
		b.u = 7 - offset.getX();
		b.v = offset.getZ();
		b.pos = r.getBlockPos();
		selectedBlocks.add(b);
	}

	private double[] texureMinX, texureMaxX;
	private double[] texureMinY, texureMaxY;

	public void render(double x, double y, double z, Side side) {
		if (selectedBlocks.size() < 1) {
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
			renderVectors(vb, u, side);
		}
		vb.setTranslation(0, 0, 0);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderVectors(VertexBuffer vb, SelectBlock info, Side side) {
		double x = info.pos.getX();
		double y = info.pos.getY() + 1.001;
		double z = info.pos.getZ();

		double u = TEXTURE_OFFSETS[info.u];
		double v = TEXTURE_OFFSETS[info.v];

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
	}

	private static Side castSide(Boolean side) {
		if (side != null && side) {
			return Side.BLACK;
		} else {
			return Side.WHITE;
		}
	}

	public static Side getSide(ItemStack stack) {
		Boolean b = null;
		if (stack.hasTagCompound()) {
			b = stack.getTagCompound().getBoolean(NBT_SIDE);
		}
		return castSide(b);
	}
}
