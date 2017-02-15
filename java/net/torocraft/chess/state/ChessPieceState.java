package net.torocraft.chess.state;

import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;

public class ChessPieceState {
	public static enum ChessPeiceType {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public ChessPieceState.ChessPeiceType type;
	public String position;

	public static ChessPieceState fromEntity(EntityChessPiece entity) {
		ChessPieceState state = new ChessPieceState();

		if (entity instanceof EntityBishop) {
			state.type = ChessPeiceType.BISHOP;

		} else if (entity instanceof EntityKing) {
			state.type = ChessPeiceType.KING;

		} else if (entity instanceof EntityKnight) {
			state.type = ChessPeiceType.KNIGHT;

		} else if (entity instanceof EntityQueen) {
			state.type = ChessPeiceType.QUEEN;

		} else if (entity instanceof EntityRook) {
			state.type = ChessPeiceType.ROOK;

		} else {
			state.type = ChessPeiceType.PAWN;
		}

		state.position = entity.getChessPosition();

		return state;
	}
}