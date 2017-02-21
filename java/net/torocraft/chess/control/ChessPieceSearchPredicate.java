package net.torocraft.chess.control;

import java.util.UUID;

import com.google.common.base.Predicate;

import net.torocraft.chess.enities.EntityChessPiece;

public class ChessPieceSearchPredicate implements Predicate<EntityChessPiece> {

	private final UUID gameId;

	public ChessPieceSearchPredicate(UUID gameId) {
		this.gameId = gameId;
	}

	@Override
	public boolean apply(EntityChessPiece e) {
		if (e.getChessPosition() == null || e.getGameId() == null) {
			return false;
		}
		return e.getGameId().equals(gameId);
	}
}