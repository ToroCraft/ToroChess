package net.torocraft.chess.engine.checkers;

import net.torocraft.chess.engine.GamePieceState;

public class CheckersPieceState extends GamePieceState {
	public enum Type {
		CHECKER, KING
	}

	public Type type;

}