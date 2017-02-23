package net.torocraft.chess.engine.chess;

import net.torocraft.chess.engine.GamePieceState;

public class ChessPieceState extends GamePieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public Type type;

	public ChessPieceState() {

    }

	public ChessPieceState(ChessPieceState chessPieceState) {
	    this.type = chessPieceState.type;
	    this.side = chessPieceState.side;
	    this.isInitialMove = chessPieceState.isInitialMove;
	    this.position = new Position(chessPieceState.position);
    }
}