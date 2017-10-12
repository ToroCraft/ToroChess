package net.torocraft.chess.engine.chess.impl;

import static net.torocraft.chess.engine.GamePieceState.Move;
import static net.torocraft.chess.engine.GamePieceState.Position;
import static net.torocraft.chess.engine.GamePieceState.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessAIEngine;

public class RandomAIEngine implements IChessAIEngine {

  private Move aiMove;
  private Map<ChessPieceState, ChessMoveResult> moveResultsMap;
  private Side sideToMove;
  private List<ChessPieceState> state;
  private ChessRuleEngine ruleEngine;
  private Move currentMoveToMake;
  private ChessPieceState currentPieceToEat;

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

    checkIfAnyMoveToTakePiece();
    if (currentMoveToMake != null) {
      aiMove = currentMoveToMake;
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

  private void checkIfAnyMoveToTakePiece() {
    currentMoveToMake = null;
    currentPieceToEat = null;
    List<ChessPieceState> keys = new ArrayList<>(moveResultsMap.keySet());
    for (ChessPieceState key : keys) {
      checkLegalPositionsForKey(key);
    }
  }

  private void checkLegalPositionsForKey(ChessPieceState key) {
    for (Position position : moveResultsMap.get(key).legalPositions) {
      checkStatePiecesAtPosition(position, key);
    }
  }

  private void checkStatePiecesAtPosition(Position position, ChessPieceState key) {
    for (ChessPieceState statePiece : state) {
      if (position.equals(statePiece.position)) {
        if (isCurrentPieceHigherRanked(statePiece)) {
          currentPieceToEat = statePiece;
          currentMoveToMake = new Move(key.position, position);
        }
      }
    }
  }

  private boolean isCurrentPieceHigherRanked(ChessPieceState statePiece) {
    return currentMoveToMake == null || statePiece.getRanking() > currentPieceToEat.getRanking();
  }
}
