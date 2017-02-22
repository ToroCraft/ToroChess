package net.torocraft.chess.engine.chess;

import net.torocraft.chess.engine.GamePieceState;

public class ChessPieceState extends GamePieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public Type type;

}