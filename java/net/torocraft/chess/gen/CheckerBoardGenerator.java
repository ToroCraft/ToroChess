package net.torocraft.chess.gen;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class CheckerBoardGenerator {

	private final World world;

	/**
	 * the coordinates of board position a8
	 */
	private final BlockPos a8;
	private IBlockState whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
	private IBlockState blackBlock = Blocks.OBSIDIAN.getDefaultState();

	private static final IBlockState STAIRS_NORTH = stairsBlock(EnumFacing.NORTH);
	private static final IBlockState STAIRS_SOUTH = stairsBlock(EnumFacing.SOUTH);
	private static final IBlockState STAIRS_WEST = stairsBlock(EnumFacing.WEST);
	private static final IBlockState STAIRS_EAST = stairsBlock(EnumFacing.EAST);
	private static final IBlockState BORDER = border();

	/*
	 * Cursor Variables (relative to a8)
	 */
	private int x;
	private int y;
	private int z;
	private IBlockState block;

	/*
	 * Draw Flags
	 */
	private boolean blockWasDrawable;
	private boolean onlyPlaceIfAir = false;

	public CheckerBoardGenerator(World world, BlockPos position) {
		this.world = world;
		a8 = position;
	}

	public void generate() {
		if (world.isRemote) {
			return;
		}
		placeCheckerBlocks();
		placeBorderBlocks();
		placeBorderStairs();
		placePodiums();
		// TODO clear top
	}

	private static final BlockPos WHITE_PODIUM = new BlockPos(3, -1, -2);
	private static final BlockPos BLACK_PODIUM = new BlockPos(3, -1, 9);

	private void placePodiums() {
		setCursor(WHITE_PODIUM);
		placePodium(EnumFacing.SOUTH);

		setCursor(BLACK_PODIUM);
		placePodium(EnumFacing.NORTH);
	}

	private void setCursor(BlockPos pos) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	private void placePodium(EnumFacing facing) {
		block = BORDER;
		drawLine(Axis.X, 2);

		block = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing);
		y++;
		drawLine(Axis.X, -2);
	}

	public ILockableContainer getWhiteChest() {
		setCursor(WHITE_PODIUM);
		y++;
		return getChestAtCursor();
	}

	private ILockableContainer getChestAtCursor() {
		return ((BlockChest) world.getBlockState(cursorCoords()).getBlock()).getLockableContainer(world, cursorCoords());
	}

	public ILockableContainer getBlackChest() {
		setCursor(BLACK_PODIUM);
		y++;
		return getChestAtCursor();
	}

	private void placeBorderBlocks() {
		block = BORDER;
		y = 0;
		z = x = -1;
		drawLine(Axis.X, 10);
		drawLine(Axis.Z, 10);
		drawLine(Axis.X, -10);
		drawLine(Axis.Z, -10);
	}

	private void placeBorderStairs() {
		z = x = -2;
		y = 0;
		int length = 12;

		drawStairBorder(length);

		onlyPlaceIfAir = true;

		for (int i = 0; i < 50; i++) {
			z--;
			x--;
			y--;
			length += 2;
			if (!drawStairBorder(length)) {
				break;
			}
		}

		onlyPlaceIfAir = false;
	}

	private boolean drawStairBorder(int length) {
		boolean somethingDrawn = false;

		block = STAIRS_SOUTH;
		somethingDrawn = drawLine(Axis.X, length) || somethingDrawn;

		block = STAIRS_WEST;
		somethingDrawn = drawLine(Axis.Z, length) || somethingDrawn;

		block = STAIRS_NORTH;
		somethingDrawn = drawLine(Axis.X, -length) || somethingDrawn;

		block = STAIRS_EAST;
		somethingDrawn = drawLine(Axis.Z, -length) || somethingDrawn;

		return somethingDrawn;
	}

	private boolean drawLine(Axis axis, int length) {
		int l = computeTravelDistance(length);
		boolean isPositive = length >= 0;
		boolean somethingDrawn = false;
		for (int i = 0; i < l; i++) {
			placeBlock();
			if (i < l - 1) {
				if (isPositive) {
					incrementAxis(axis, 1);
				} else {
					incrementAxis(axis, -1);
				}
			}

			somethingDrawn = somethingDrawn || blockWasDrawable;
		}

		return somethingDrawn;
	}

	private int computeTravelDistance(int length) {
		return Math.abs(length);
	}

	private void incrementAxis(Axis axis, int amount) {
		switch (axis) {
		case X:
			x += amount;
			break;
		case Y:
			y += amount;
			break;
		case Z:
			z += amount;
			break;
		default:
			break;
		}
	}

	private void placeCheckerBlocks() {
		y = 0;
		for (x = 0; x < 8; x++) {
			for (z = 0; z < 8; z++) {
				block = defineCheckerBlock();
				placeBlock();
			}
		}
	}

	private void placeBlock() {
		if (okToPlaceBlock()) {
			blockWasDrawable = true;
			world.setBlockState(cursorCoords(), block);
		} else {
			blockWasDrawable = false;
		}
	}

	private boolean okToPlaceBlock() {
		return !onlyPlaceIfAir || onAirBlock();
	}

	private boolean onAirBlock() {
		IBlockState currentBlock = world.getBlockState(cursorCoords());
		return !currentBlock.isOpaqueCube();
	}

	/**
	 * Get the Minecraft coordinates of the cursor
	 */
	private BlockPos cursorCoords() {
		return a8.add(x, y, z);
	}

	private IBlockState defineCheckerBlock() {
		if (isWhiteBlock()) {
			return whiteBlock;
		} else {
			return blackBlock;
		}
	}

	private boolean isWhiteBlock() {
		return (x + z) % 2 == 0;
	}

	private static IBlockState border() {
		return Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
	}

	private static IBlockState stairsBlock(EnumFacing facing) {
		return Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, facing);
	}

	public IBlockState getWhiteBlock() {
		return whiteBlock;
	}

	public void setWhiteBlock(IBlockState whiteBlock) {
		this.whiteBlock = whiteBlock;
	}

	public IBlockState getBlackBlock() {
		return blackBlock;
	}

	public void setBlackBlock(IBlockState blackBlock) {
		this.blackBlock = blackBlock;
	}
}
