package net.torocraft.chess.items;


import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;

public class ItemChessControlWand extends Item {

	public static ItemChessControlWand INSTANCE;

	public static String NAME = "chess_control_wand";

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
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {

		ItemStack stack = player.getHeldItem(hand);
		
		if(world.isRemote || !stack.hasTagCompound()){
			return EnumActionResult.PASS;
		}
		
		NBTTagCompound c = stack.getTagCompound();
		if(!c.hasKey(NBT_A1_POS) || !c.hasKey(NBT_SELECTED_POS)){
			return EnumActionResult.PASS;
		}
		
		String piecePos = c.getString(NBT_SELECTED_POS);
		BlockPos a1Pos = BlockPos.fromLong(stack.getTagCompound().getLong(NBT_A1_POS));
		
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(CheckerBoardUtil.getPosition(a1Pos, piecePos)).expand(0, 1, 0));
		
		if(pieces == null || pieces.size() < 1){
			return EnumActionResult.PASS;
		}
		
		pieces.get(0).setChessPosition(CheckerBoardUtil.getPositionName(a1Pos, pos));

		System.out.println(" ************* onItemUse " + CheckerBoardUtil.getPositionName(a1Pos, pos));

		return EnumActionResult.PASS;
	}

	private static final String NBT_SELECTED_PIECE = "piece";
	private static final String NBT_SELECTED_POS = "pos";
	private static final String NBT_A1_POS = "pos";

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {

		if (player.world.isRemote || !(target instanceof EntityChessPiece)) {
			return true;
		}

		ItemStack stack = player.getHeldItem(hand);
		
		NBTTagCompound c = stack.getTagCompound();
		if (c == null) {
			c = new NBTTagCompound();
		} else {
			c.removeTag(NBT_SELECTED_PIECE);
			c.removeTag(NBT_SELECTED_POS);
			c.removeTag(NBT_A1_POS);
		}

		if (target.isPotionActive(MobEffects.GLOWING)) {
			target.removeActivePotionEffect(MobEffects.GLOWING);
		} else {
			PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false);
			target.addPotionEffect(potioneffect);
			c.setString(NBT_SELECTED_POS, ((EntityChessPiece) target).getChessPosition());
			c.setLong(NBT_SELECTED_POS, ((EntityChessPiece) target).getA1Pos().toLong());
			c.setString(NBT_SELECTED_PIECE, target.getName());
		}
		
		stack.setTagCompound(c);
		
		System.out.println(stack.getTagCompound() + " "  + stack.hasTagCompound());
	
		return true;
	}

}
