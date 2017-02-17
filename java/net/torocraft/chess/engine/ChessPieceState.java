package net.torocraft.chess.engine;

public class ChessPieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public enum Side {
		WHITE, BLACK
	}

	public enum CoordinateLetter {
		A, B, C, D, E, F, G, H
	}

	public enum CoordinateNumber {
		One, Two, Three, Four, Five, Six, Seven, Eight
	}

	public Type type;
	public Position position;
	public Side side;

	public static class Position {
		public CoordinateLetter letter;
		public CoordinateNumber number;
	}
}