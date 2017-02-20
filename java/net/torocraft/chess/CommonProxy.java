package net.torocraft.chess;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.blocks.TileEntityChessControl;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.pawn.EntityPawn;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		ExtendedReachHandler.init();
		ItemChessControlWand.init();
	}

	public void init(FMLInitializationEvent e) {
		int id = 0;
		EntityBishop.init(id++);
		EntityKing.init(id++);
		EntityKnight.init(id++);
		EntityPawn.init(id++);
		EntityQueen.init(id++);
		EntityRook.init(id++);
		TileEntityChessControl.init();
		BlockChessControl.init();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
