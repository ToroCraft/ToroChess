package net.torocraft.chess.engine.impl;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessAIEngine;

import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Move;
import static net.torocraft.chess.engine.ChessPieceState.Side;
import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.File;
import static net.torocraft.chess.engine.ChessPieceState.Rank;

public class ChessAIEngine implements IChessAIEngine {
    private Move aiMove;

    @Override
    public Move getAIMove(List<ChessPieceState> state, Side sideToMove) {
        //TODO write AI

        //FIXME test data
        aiMove = new Move(new Position(File.A, Rank.EIGHT),
                new Position(File.D, Rank.FOUR));
        return aiMove;
    }
}
