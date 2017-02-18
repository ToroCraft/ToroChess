package net.torocraft.chess.engine.impl;

import java.util.ArrayList;
import java.util.List;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.IChessRuleEngine;
import net.torocraft.chess.engine.MoveResult;

public class TestingHighlightPiecesRuleEngine implements IChessRuleEngine {
	@Override
	public MoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
		MoveResult r = new MoveResult();
		r.legalPositions = new ArrayList<>();
		for (ChessPieceState pieceState : state) {
			r.legalPositions.add(pieceState.position);
		}
		return r;
	}
}