package net.torocraft.chess;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.MessageChessControl;
import net.torocraft.chess.control.MessageChessSetPlayMode;
import net.torocraft.chess.control.MessageLegalMovesRequest;
import net.torocraft.chess.control.MessageLegalMovesResponse;
import net.torocraft.chess.control.MessageTurnChangeEvent;
import net.torocraft.chess.control.MessageUpdateControlBlock;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.entities.bishop.EntityBishop;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.knight.EntityKnight;
import net.torocraft.chess.entities.pawn.EntityPawn;
import net.torocraft.chess.entities.queen.EntityQueen;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;
import net.torocraft.chess.items.extendedreach.ExtendedReachHandler;
import net.torocraft.chess.items.extendedreach.MessageExtendedReachInteract;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent e) {
    int packetId = 0;
    MessageExtendedReachInteract.init(packetId++);
    MessageChessControl.init(packetId++);
    MessageLegalMovesRequest.init(packetId++);
    MessageLegalMovesResponse.init(packetId++);
    MessageTurnChangeEvent.init(packetId++);
    MessageChessSetPlayMode.init(packetId++);
    MessageUpdateControlBlock.init(packetId++);

    ExtendedReachHandler.init();
    ToroChessGuiHandler.init();
    ItemChessControlWand.init();
    BlockChessControl.init();
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

  }

  public void postInit(FMLPostInitializationEvent e) {

  }
}
