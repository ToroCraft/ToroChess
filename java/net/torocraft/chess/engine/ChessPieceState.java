package net.torocraft.chess.engine;

import net.torocraft.chess.enities.IChessPiece.Side;

public class ChessPieceState {
	
	public static enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public Type type;
	public String position;
	public Side side;
	
}