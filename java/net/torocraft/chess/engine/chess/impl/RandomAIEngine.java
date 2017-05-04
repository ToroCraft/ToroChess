package net.torocraft.chess.engine.chess.impl;

import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessAIEngine;

import static net.torocraft.chess.engine.GamePieceState.Side;
import static net.torocraft.chess.engine.GamePieceState.Move;
import static net.torocraft.chess.engine.GamePieceState.Position;

import java.util.*;

public class RandomAIEngine implements IChessAIEngine {

	private Move aiMove;
	private Map<ChessPieceState, ChessMoveResult> moveResultsMap;
	private Side sideToMove;
	private List<ChessPieceState> state;
	private ChessRuleEngine ruleEngine;

	@Override
	public Move getAIMove(List<ChessPieceState> state, Side sideToMove) {
		this.sideToMove = sideToMove;
		this.state = state;
		ruleEngine = new ChessRuleEngine();

		getFullMoveResults();
		chooseRandomMove();

		return aiMove;
	}

	private void getFullMoveResults() {
		ChessMoveResult moveResult;
		moveResultsMap = new HashMap<>();
		for (ChessPieceState chessPieceState : state) {
			if (chessPieceState.side.equals(sideToMove)) {
				moveResult = ruleEngine.getMoves(state, chessPieceState);

				if (moveResult.legalPositions.size() > 0) {
					moveResultsMap.put(chessPieceState, moveResult);
				}
			}
		}
	}

	private void chooseRandomMove() {
		Random random = new Random();
		List<ChessPieceState> keys = new ArrayList<>(moveResultsMap.keySet());

		if (keys == null || keys.size() < 1) {
			return;
		}

		ChessPieceState randomChessPieceToMoveThatIsLegal = keys.get(random.nextInt(keys.size()));
		ChessMoveResult randomMoveResult = moveResultsMap.get(randomChessPieceToMoveThatIsLegal);

		if (randomMoveResult == null || randomMoveResult.legalPositions == null || randomMoveResult.legalPositions.size() < 1) {
			return;
		}

		random = new Random();
		Position randomPositionToMoveToThatIsLegal = randomMoveResult.legalPositions.get(random.nextInt(randomMoveResult.legalPositions.size()));

		aiMove = new Move(randomChessPieceToMoveThatIsLegal.position, randomPositionToMoveToThatIsLegal);
	}
}
