package net.torocraft.chess.engine.impl;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessAIEngine;

import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Move;
import static net.torocraft.chess.engine.ChessPieceState.Side;
import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.CoordinateLetter;
import static net.torocraft.chess.engine.ChessPieceState.CoordinateNumber;

public class ChessAIEngine implements IChessAIEngine {
    private Move aiMove;

    @Override
    public Move getAIMove(List<ChessPieceState> state, Side sideToMove) {
        //TODO write AI

        //FIXME test data
        aiMove = new Move(new Position(CoordinateLetter.A, CoordinateNumber.Eight),
                new Position(CoordinateLetter.D, CoordinateNumber.Four));
        return aiMove;
    }
}
