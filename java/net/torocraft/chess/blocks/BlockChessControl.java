package net.torocraft.chess.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.gen.ChessGame;

public class BlockChessControl extends Block {

	public static final String NAME = "chess_control";

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
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(ToroChess.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	private ChessGame game;
	private TileEntity tileEntity;

	private boolean isOn = false;
	private boolean wasOn = false;

	public BlockChessControl() {
		super(Material.GROUND);
		setUnlocalizedName(NAME);
		setResistance(0.1f);
		setHardness(0.5f);
		setLightLevel(0);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		getGame().placePieces();
	}

	public ChessGame getGame() {

		if (game == null) {
			game = new ChessGame(tileEntity.getWorld(), tileEntity.getPos());
		}

		return game;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		game = new ChessGame(worldIn, pos);

		if (!worldIn.isRemote) {
			game.generate();
		}

		if (placer != null) {
			placer.move(MoverType.SELF, 0, 2, 0);
		}
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (worldIn.isRemote) {
			return;
		}
		updateOnState(worldIn, pos);
		if (isTurningOn()) {
			System.out.println("placePieces");
			getGame().placePieces();
		} else {
			worldIn.scheduleUpdate(pos, this, 8);
		}
	}

	private void updateOnState(World worldIn, BlockPos pos) {
		wasOn = isOn;
		isOn = worldIn.isBlockPowered(pos);
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) {
			return;
		}

		if (!worldIn.isBlockPowered(pos)) {
			wasOn = false;
			isOn = false;
		}
	}

	private boolean isTurningOn() {
		return !wasOn && isOn;
	}

}
