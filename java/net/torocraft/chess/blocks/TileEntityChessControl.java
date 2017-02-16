package net.torocraft.chess.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityChessControl extends TileEntity {

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityChessControl.class, "chess_control_tile_entity");
	}

}
