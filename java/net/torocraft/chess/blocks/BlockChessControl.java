package net.torocraft.chess.blocks;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.ToroChessGuiHandler;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.gen.ChessGameGenerator;

public class BlockChessControl extends BlockContainer {

	public static final String NBT_TYPE = "chesstype";
	public static final String NAME = "chess_control";
	public static final BlockPos A8_CENTER_OFFSET = new BlockPos(-4, 1, -4);

	private static final String NBT_A8_KEY = "chess_a8";
	private static final String NBT_GAME_ID_KEY = "chess_game_id";

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

		GameRegistry.addRecipe(new ChessControlRecipe());
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
		if (!blockHasAlreadyBeenPlaced(stack)) {
			if (!world.isRemote) {
				generateChessBoard(world, pos, placer, stack);
			}

			if (placer != null) {
				placer.move(MoverType.SELF, 0, 2, 0);
			}
		} else {
			BlockPos a8 = BlockPos.fromLong(stack.getTagCompound().getLong(NBT_A8_KEY));
			UUID gameId = stack.getTagCompound().getUniqueId(NBT_GAME_ID_KEY);
			setGameDataToTileEntity(world, pos, a8, gameId);
		}
	}

	private boolean blockHasAlreadyBeenPlaced(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			return false;
		}

		NBTTagCompound c = stack.getTagCompound();

		if (!c.hasKey(NBT_A8_KEY)) {
			return false;
		}

		return true;
	}

	private void generateChessBoard(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack) {
		BlockPos a8 = pos.add(A8_CENTER_OFFSET);
		UUID gameId = UUID.randomUUID();
		NBTTagCompound c = stack.getTagCompound();

		IBlockState whiteBlock = null;
		IBlockState blackBlock = null;

		if (c != null) {
			NonNullList<ItemStack> list = NonNullList.<ItemStack> withSize(2, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(c, list);
			whiteBlock = getBlockState(world, pos, placer, list.get(0));
			blackBlock = getBlockState(world, pos, placer, list.get(1));
		}
		new ChessGameGenerator(world, a8, pos, gameId, whiteBlock, blackBlock).generate();

		setGameDataToTileEntity(world, pos, a8, gameId);
	}

	private void setGameDataToTileEntity(World world, BlockPos pos, BlockPos a8, UUID gameId) {
		TileEntityChessControl control = ((TileEntityChessControl) world.getTileEntity(pos));
		control.setA8(a8);
		control.setGameId(gameId);
		control.setSelectedPiece(null);
		control.setTurn(Side.WHITE);
		control.markDirty();
	}

	private IBlockState getBlockState(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
			return null;
		}

		return ((ItemBlock) stack.getItem()).getBlock().getStateForPlacement(world, pos, EnumFacing.DOWN, 0f, 0f, 0f, stack.getMetadata(), placer,
				EnumHand.MAIN_HAND);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChessControl();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityChessControl control = (TileEntityChessControl) world.getTileEntity(pos);
		control.clearBoard();
		spawnAsEntity(world, pos, getDropWithTileEntityData(control));
		super.breakBlock(world, pos, state);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new java.util.ArrayList<ItemStack>();
	}

	public ItemStack getDropWithTileEntityData(TileEntityChessControl control) {

		if (control == null) {
			throw new NullPointerException();
		}

		NBTTagCompound c = new NBTTagCompound();
		c.setLong(NBT_A8_KEY, control.getA8().toLong());
		c.setUniqueId(NBT_GAME_ID_KEY, control.getGameId());

		ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
		stack.setTagCompound(c);

		return stack;
	}

	@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_A8_KEY)) {
			tooltip.add(BlockPos.fromLong(stack.getTagCompound().getLong(NBT_A8_KEY)).toString());
			tooltip.add(stack.getTagCompound().getUniqueId(NBT_GAME_ID_KEY).toString());
		}
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ) {

		if (!world.isRemote) {
			return true;
		}

		player.openGui(ToroChess.INSTANCE, ToroChessGuiHandler.CHESS_CONTROL_GUI, world, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}

}
