package net.torocraft.chess;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.CheckerBoardOverlay;
import net.torocraft.chess.entities.bishop.EntityBishop;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.knight.EntityKnight;
import net.torocraft.chess.entities.pawn.EntityPawn;
import net.torocraft.chess.entities.queen.EntityQueen;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(FMLPreInitializationEvent e) {
    super.preInit(e);
    CheckerBoardOverlay.init();
    EntityBishop.registerRenders();
    EntityKing.registerRenders();
    EntityKnight.registerRenders();
    EntityPawn.registerRenders();
    EntityQueen.registerRenders();
    EntityRook.registerRenders();
  }

  @Override
  public void init(FMLInitializationEvent e) {
    super.init(e);
    BlockChessControl.registerRenders();
    ItemChessControlWand.registerRenders();
  }

  @Override
  public void postInit(FMLPostInitializationEvent e) {
    super.postInit(e);
  }

}