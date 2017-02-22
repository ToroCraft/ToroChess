package net.torocraft.chess.engine.chess;

import net.torocraft.chess.engine.GamePieceState;

public class ChessPieceState extends GamePieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	// TODO add deep copy for king check testing purposes
}