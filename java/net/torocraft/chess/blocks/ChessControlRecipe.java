package net.torocraft.chess.blocks;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ChessControlRecipe implements IRecipe {

	public static final String NBT_WHITE_BLOCK_KEY = "white_checker_block";
	public static final String NBT_BLACK_BLOCK_KEY = "black_checker_block";
	private static final int INDEX_WHITE_BLOCK = 3;
	private static final int INDEX_BLACK_BLOCK = 5;

	private static final Block[] VALID_BOARD_BLOCKS = { Blocks.OBSIDIAN, Blocks.PLANKS, Blocks.PLANKS, Blocks.STAINED_GLASS,
			Blocks.STAINED_HARDENED_CLAY, Blocks.WOOL, Blocks.DIRT, Blocks.QUARTZ_BLOCK, Blocks.LOG, Blocks.LOG2, Blocks.OBSIDIAN, Blocks.STONE,
			Blocks.STONEBRICK, Blocks.COBBLESTONE, Blocks.BONE_BLOCK, Blocks.COAL_BLOCK, Blocks.NETHER_BRICK, Blocks.MELON_BLOCK, Blocks.SANDSTONE,
			Blocks.MOSSY_COBBLESTONE };

	private final ItemStack output = new ItemStack(BlockChessControl.INSTANCE);

	public ItemStack getRecipeOutput() {
		return output;
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.<ItemStack> withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	public boolean matches(InventoryCrafting inv, World worldIn) {

		int size = inv.getHeight() * inv.getWidth();

		if (size < 9) {
			return false;
		}

		for (int index = 0; index < 9; index++) {
			if (!check(index, inv.getStackInSlot(index))) {
				return false;
			}
		}

		return true;
	}

	private boolean check(int index, ItemStack stack) {

		if (index > 5) {
			if (!isBlock(stack, Blocks.DIAMOND_BLOCK)) {
				return false;
			}
		} else if (index < 3) {
			if (!isEntityEssenceItem(stack)) {
				return false;
			}

		} else if (index == INDEX_WHITE_BLOCK || index == INDEX_BLACK_BLOCK) {
			if (!isValidBoardBlock(stack)) {
				return false;
			}

		} else if (index == 4) {
			if (stack.getItem() != Items.GOLDEN_SWORD) {
				return false;
			}

		} else {
			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private boolean isEntityEssenceItem(ItemStack stack) {
		return stack.getItem() == Items.BONE || stack.getItem() == Items.ROTTEN_FLESH || stack.getItem() == Items.ENDER_PEARL;
	}

	private boolean isValidBoardBlock(ItemStack stack) {
		Block block = getBlock(stack);

		if (block == null) {
			return false;
		}

		return Arrays.asList(VALID_BOARD_BLOCKS).contains(block);
	}

	private Block getBlock(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}

		if (!(stack.getItem() instanceof ItemBlock)) {
			return null;
		}

		return ((ItemBlock) stack.getItem()).getBlock();
	}

	private boolean isBlock(ItemStack stack, Block block) {
		return getBlock(stack) == block;
	}

	public ItemStack getCraftingResult(InventoryCrafting inv) {
		NonNullList<ItemStack> list = NonNullList.<ItemStack> withSize(2, ItemStack.EMPTY);
		list.set(0, inv.getStackInSlot(INDEX_WHITE_BLOCK));
		list.set(1, inv.getStackInSlot(INDEX_BLACK_BLOCK));

		NBTTagCompound c = new NBTTagCompound();
		ItemStackHelper.saveAllItems(c, list);

		ItemStack output = getRecipeOutput().copy();
		output.setTagCompound(c);
		return output;
	}

	public int getRecipeSize() {
		return 9;
	}
}