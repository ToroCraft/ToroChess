package net.torocraft.chess.engine;

public class ChessPieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public enum Side {
		WHITE, BLACK
	}

	public Type type;
	public String position;
	public Side side;
}