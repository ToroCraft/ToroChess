package net.torocraft.chess.blocks;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.ToroChessGuiHandler;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.gen.ChessGameGenerator;

public class BlockChessControl extends BlockContainer {

	public static final String NBT_TYPE = "chesstype";
	public static final String NAME = "chess_control";
	public static final BlockPos A8_OFFSET = new BlockPos(-4, 1, -4);

	public static BlockChessControl INSTANCE;
	public static Item ITEM_INSTANCE;

	public static void init() {
		INSTANCE = new BlockChessControl();
		ResourceLocation resourceName = new ResourceLocation(ToroChess.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);

		// initRecipes();
	}

	public static void initRecipes() {

		// addRecipe(Blocks.QUARTZ_BLOCK, Blocks.OBSIDIAN,
		// EnumType.QUARTZ_OBSIDIAN);
		addRecipe(Blocks.GLASS, Blocks.GLASS, EnumType.GLASS);

		// addRecipe(Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR,
		// EnumDyeColor.WHITE),
		// Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR,
		// EnumDyeColor.BLACK), EnumType.GLASS);
	}

	private static void addRecipe(Block white, Block black, EnumType type) {

		ItemStack controlBlock = new ItemStack(BlockChessControl.ITEM_INSTANCE);
		NBTTagCompound c = new NBTTagCompound();
		c.setInteger(NBT_TYPE, type.getMetadata());
		controlBlock.setTagCompound(c);

		System.out.println("created a recipe for meta " + type);

		/*
		 * GameRegistry.addRecipe(controlBlock,
		 * 
		 * "   ", "BSF", "WDB",
		 * 
		 * 'D', new ItemStack(Blocks.DIAMOND_BLOCK),
		 * 
		 * 'W', new ItemStack(white, 32),
		 * 
		 * 'B', new ItemStack(black, 32),
		 * 
		 * 'S', new ItemStack(Items.GOLDEN_SWORD),
		 * 
		 * 'B', new ItemStack(Items.BONE, 32),
		 * 
		 * 'F', new ItemStack(Items.ROTTEN_FLESH, 32));
		 */

		GameRegistry.addShapelessRecipe(controlBlock,

				new ItemStack(white, 32));

		// GameRegistry.addRecipe(controlBlock,
		//
		// " ", " ", " D ",
		//
		// 'D', new ItemStack(Blocks.DIAMOND_BLOCK));
	}

	public static class ChessControlRecipe implements IRecipe {

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRecipeSize() {
			return 10;
		}

		@Override
		public ItemStack getRecipeOutput() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(ToroChess.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	public BlockChessControl() {
		super(Material.GROUND);
		setUnlocalizedName(NAME);
		setResistance(0.1f);
		setHardness(0.5f);
		setLightLevel(0);
		setCreativeTab(CreativeTabs.MISC);
		isBlockContainer = true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer != null) {
			placer.move(MoverType.SELF, 0, 2, 0);
		}
		if (!world.isRemote) {
			BlockPos a8 = pos.add(A8_OFFSET);

			BlockChessControl.EnumType type = BlockChessControl.EnumType.QUARTZ_OBSIDIAN;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TYPE)) {
				type = BlockChessControl.EnumType.byMetadata(stack.getTagCompound().getInteger(NBT_TYPE));
			}

			System.out.println("onBlockPlacedBy: type=" + type);

			new ChessGameGenerator(world, a8, type).generate();
			((TileEntityChessControl) world.getTileEntity(pos)).setA8(a8);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChessControl();
	}

	public static TileEntityChessControl getChessControl(World world, BlockPos a8, UUID gameId) {
		if (gameId == null) {
			return null;
		}
		TileEntityChessControl e = getChessControl(world, a8);
		if (e == null || !e.getGameId().equals(gameId)) {
			return null;
		}
		return e;
	}

	public static TileEntityChessControl getChessControl(World world, BlockPos a8) {
		if (world == null || a8 == null) {
			return null;
		}
		TileEntityChessControl e = (TileEntityChessControl) world.getTileEntity(a8.subtract(A8_OFFSET));
		if (e == null) {
			return null;
		}
		return e;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityChessControl control = (TileEntityChessControl) world.getTileEntity(pos);
		control.clearBoard();
		super.breakBlock(world, pos, state);
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ) {

		if (!world.isRemote) {
			return true;
		}

		player.openGui(ToroChess.INSTANCE, ToroChessGuiHandler.CHESS_CONTROL_GUI, world, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}

	public static enum EnumType implements IStringSerializable {

		QUARTZ_OBSIDIAN(0, "quartz_obsidian"),
		GLASS(1, "glass"),
		WOOD(2, "wood"),
		METAL(3, "metal"),
		DIAMOND_LAPIS(4, "lapis"),
		NETHER(5, "nether"),
		DIORITE_GRANITE(6, "diorite_granite"),
		WOOL(7, "wool"),
		ENDSTONE_GLOWSTONE(8, "endstone_glowstone");

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;

		private EnumType(int metaIn, String nameIn) {
			this(metaIn, nameIn, nameIn);
		}

		private EnumType(int metaIn, String nameIn, String unlocalizedNameIn) {
			this.meta = metaIn;
			this.name = nameIn;
			this.unlocalizedName = unlocalizedNameIn;
		}

		public int getMetadata() {
			return this.meta;
		}

		public String toString() {
			return this.name;
		}

		public static BlockChessControl.EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (BlockChessControl.EnumType type : values()) {
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}
}
