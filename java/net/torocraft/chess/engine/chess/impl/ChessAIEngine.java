package net.torocraft.chess.engine.chess.impl;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessAIEngine;

import static net.torocraft.chess.engine.chess.ChessPieceState.File;
import static net.torocraft.chess.engine.chess.ChessPieceState.Move;
import static net.torocraft.chess.engine.chess.ChessPieceState.Position;
import static net.torocraft.chess.engine.chess.ChessPieceState.Rank;
import static net.torocraft.chess.engine.chess.ChessPieceState.Side;

import java.util.List;

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
