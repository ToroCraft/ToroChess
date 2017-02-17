package net.torocraft.chess;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.pawn.EntityPawn;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		EntityBishop.registerRenders();
		EntityKing.registerRenders();
		EntityKnight.registerRenders();
		EntityPawn.registerRenders();
		EntityQueen.registerRenders();
		EntityRook.registerRenders();
		ItemChessControlWand.registerRenders();
		CheckerBoardOverlay.init();
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		BlockChessControl.registerRenders();
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

}