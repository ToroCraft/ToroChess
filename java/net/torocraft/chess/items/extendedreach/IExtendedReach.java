package net.torocraft.chess.items.extendedreach;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IExtendedReach {
	public float getReach();

	EnumActionResult onItemUseExtended(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ);

	boolean itemInteractionForEntityExtended(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand);

}