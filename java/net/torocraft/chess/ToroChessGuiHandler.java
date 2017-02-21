package net.torocraft.chess;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.torocraft.chess.control.gui.ChessControlGui;

public class ToroChessGuiHandler implements IGuiHandler {

	public static final int CHESS_CONTROL_GUI = 0;

	public static void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(ToroChess.INSTANCE, new ToroChessGuiHandler());
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == CHESS_CONTROL_GUI) {
			return new ChessControlGui(new BlockPos(x, y, z));
		}
		return null;
	}

}
