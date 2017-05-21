package net.torocraft.chess.engine.chess;

import static net.torocraft.chess.engine.GamePieceState.Side;

import java.util.List;
import net.torocraft.chess.engine.IGameRuleEngine;

public interface IChessRuleEngine extends IGameRuleEngine<ChessPieceState, ChessMoveResult> {

  ChessMoveResult getBoardConditionForSide(List<ChessPieceState> state, Side sideToCheck);
}
