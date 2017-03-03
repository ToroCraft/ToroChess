package net.torocraft.chess.items;

import java.util.UUID;

import com.google.common.base.Predicate;

import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.entities.EntityChessPiece;

public class ChessPieceAtPredicate implements Predicate<EntityChessPiece> {
	private final Position chessPosition;
	private final UUID gameId;

	public ChessPieceAtPredicate(Position chessPosition, UUID gameId) {
		this.chessPosition = chessPosition;
		this.gameId = gameId;
	}

	@Override
	public boolean apply(EntityChessPiece e) {
		if (e.getChessPosition() == null || e.getGameId() == null) {
			return false;
		}
		Position p = e.getChessPosition();
		return chessPosition.file.equals(p.file) && chessPosition.rank.equals(p.rank) && e.getGameId().equals(gameId);
	}
}