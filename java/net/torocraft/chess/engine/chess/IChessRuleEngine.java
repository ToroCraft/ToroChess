package net.torocraft.chess.engine.chess;

import net.torocraft.chess.engine.IGameRuleEngine;

import java.util.List;

import static net.torocraft.chess.engine.GamePieceState.Side;

public interface IChessRuleEngine extends IGameRuleEngine<ChessPieceState, ChessMoveResult> {
	ChessMoveResult getBoardConditionForSide(List<ChessPieceState> state, Side sideToCheck);
}
