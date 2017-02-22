package net.torocraft.chess.engine.chess.impl;

import java.util.ArrayList;
import java.util.List;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.ChessMoveResult;

public class TestingHighlightPiecesRuleEngine implements IChessRuleEngine {
	@Override
	public ChessMoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
		ChessMoveResult r = new ChessMoveResult();
		r.legalPositions = new ArrayList<>();
		for (ChessPieceState pieceState : state) {
			r.legalPositions.add(pieceState.position);
		}
		return r;
	}
}