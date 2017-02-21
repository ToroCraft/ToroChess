package net.torocraft.chess.blocks;

import java.util.List;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.ToroChessGuiHandler;
import net.torocraft.chess.control.ChessPieceSearchPredicate;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.ChessGameGenerator;

public class BlockChessControl extends BlockContainer {

	public static final String NAME = "chess_control";

	public static BlockChessControl INSTANCE;

	public static Item ITEM_INSTANCE;

	private static final BlockPos A8_OFFSET = new BlockPos(-4, 1, -4);

	public static void init() {
		INSTANCE = new BlockChessControl();
		ResourceLocation resourceName = new ResourceLocation(ToroChess.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);

		GameRegistry.addRecipe(new ItemStack(BlockChessControl.ITEM_INSTANCE), " Q ", "OSQ", "   ", 'Q', new ItemStack(Blocks.QUARTZ_BLOCK, 32), 'O',
				new ItemStack(Blocks.OBSIDIAN, 32), 'S', Items.GOLDEN_SWORD);
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
			new ChessGameGenerator(world, pos.add(A8_OFFSET)).generate();
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

		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(control.getGameId()));

		for (EntityChessPiece piece : pieces) {
			piece.setDead();
		}

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
}
