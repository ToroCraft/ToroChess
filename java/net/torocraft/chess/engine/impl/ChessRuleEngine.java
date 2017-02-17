package net.torocraft.chess.engine.impl;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessRuleEngine;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.File;
import static net.torocraft.chess.engine.ChessPieceState.Rank;

public class ChessRuleEngine implements IChessRuleEngine {
    private List<Position> legalPositions;

    @Override
    public List<Position> getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        //TODO calculate if a checkmate exists
        //TODO calculate if check exists

        switch (chessPieceToMove.type) {
            case BISHOP:
                //TODO call BISHOP class
                break;
            case KING:
                //TODO call KING class
                break;
            case KNIGHT:
                //TODO call KNIGHT class
                break;
            case PAWN:
                //TODO call PAWN class
                break;
            case QUEEN:
                //TODO call QUEEN class
                break;
            case ROOK:
                //TODO call ROOK class
                break;
            default:
                legalPositions = new ArrayList<>();
                break;
        }

        //FIXME Test data return
        legalPositions.add(new Position(File.A, Rank.FOUR));
        legalPositions.add(new Position(File.B, Rank.SEVEN));
        return legalPositions;
    }
}
