package net.torocraft.chess;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.MessageChessControl;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.pawn.EntityPawn;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;
import net.torocraft.chess.items.extendedreach.ExtendedReachHandler;
import net.torocraft.chess.items.extendedreach.MessageExtendedReachInteract;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		int packetId = 0;
		MessageExtendedReachInteract.init(packetId++);
		MessageChessControl.init(packetId++);

		ExtendedReachHandler.init();
		ToroChessGuiHandler.init();
		ItemChessControlWand.init();
	}

	public void init(FMLInitializationEvent e) {
		int entityId = 0;
		EntityBishop.init(entityId++);
		EntityKing.init(entityId++);
		EntityKnight.init(entityId++);
		EntityPawn.init(entityId++);
		EntityQueen.init(entityId++);
		EntityRook.init(entityId++);
		TileEntityChessControl.init();
		BlockChessControl.init();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
