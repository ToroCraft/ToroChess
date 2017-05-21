package net.torocraft.chess.engine.chess.impl;

import static net.torocraft.chess.engine.GamePieceState.Position;
import static net.torocraft.chess.engine.chess.ChessMoveResult.Condition;
import static net.torocraft.chess.engine.chess.ChessPieceState.Side;
import static net.torocraft.chess.engine.chess.ChessPieceState.Type;

import java.util.ArrayList;
import java.util.List;
import net.torocraft.chess.engine.GamePieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.workers.BishopWorker;
import net.torocraft.chess.engine.chess.workers.ChessPieceWorker;
import net.torocraft.chess.engine.chess.workers.IChessPieceWorker;
import net.torocraft.chess.engine.chess.workers.KingWorker;
import net.torocraft.chess.engine.chess.workers.KnightWorker;
import net.torocraft.chess.engine.chess.workers.PawnWorker;
import net.torocraft.chess.engine.chess.workers.QueenWorker;
import net.torocraft.chess.engine.chess.workers.RookWorker;

//TODO add support for en passant
//TODO add support for pawn promotion
public class ChessRuleEngine implements IChessRuleEngine {

  private ChessMoveResult moveResult;
  private List<ChessPieceState> internalState;
  private ChessPieceState internalChessPieceToMove;
  private ChessPieceState currentKingState;
  private boolean isKingInCheck = false;
  private Side currentSide;

  @Override
  public Game getGameType() {
    return Game.CHESS;
  }

  @Override
  public ChessMoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
    initialize();
    internalState = state;
    internalChessPieceToMove = chessPieceToMove;
    currentSide = chessPieceToMove.side;

    currentKingState = getCurrentKingState();
    isKingInCheck = isKingInCheck();
    if (checkIfKingIsInCheckmate() || checkIfKingIsInStalemate()) {
      return moveResult;
    }

    IChessPieceWorker chessPieceWorker = getChessPieceWorker(internalState, internalChessPieceToMove);
    if (chessPieceWorker == null) {
      return new ChessMoveResult();
    }
    moveResult = chessPieceWorker.getLegalMoves();

    updateBoardCondition();
    updateMoveResult();
    return moveResult;
  }

  @Override
  public ChessMoveResult getBoardConditionForSide(List<ChessPieceState> state, Side sideToCheck) {
    initialize();
    internalState = state;
    currentSide = sideToCheck;

    currentKingState = getCurrentKingState();
    isKingInCheck = isKingInCheck();
    checkIfKingIsInCheckmate();
    checkIfKingIsInStalemate();
    updateBoardCondition();
    return moveResult;
  }

  private void initialize() {
    moveResult = new ChessMoveResult();
    internalState = null;
    internalChessPieceToMove = null;
    currentKingState = null;
    isKingInCheck = false;
    currentSide = null;
  }

  private ChessPieceState getCurrentKingState() {
    for (ChessPieceState currentChessPieceState : internalState) {
      if (currentChessPieceState.side.equals(currentSide)
          && currentChessPieceState.type.equals(Type.KING)) {
        return currentChessPieceState;
      }
    }
    return null;
  }

  private ChessPieceWorker getChessPieceWorker(List<ChessPieceState> chessPieceStateTo, ChessPieceState chessPieceToCheck) {
    if (chessPieceToCheck == null) {
      return null;
    }
    switch (chessPieceToCheck.type) {
      case BISHOP:
        return new BishopWorker(chessPieceStateTo, chessPieceToCheck);
      case KING:
        return new KingWorker(chessPieceStateTo, chessPieceToCheck);
      case KNIGHT:
        return new KnightWorker(chessPieceStateTo, chessPieceToCheck);
      case PAWN:
        return new PawnWorker(chessPieceStateTo, chessPieceToCheck);
      case QUEEN:
        return new QueenWorker(chessPieceStateTo, chessPieceToCheck);
      case ROOK:
        return new RookWorker(chessPieceStateTo, chessPieceToCheck);
      default:
        return null;
    }
  }

  private void updateBoardCondition() {
    if (isKingInCheck) {
      moveResult.kingSideCastleMove = null;
      moveResult.queenSideCastleMove = null;
      if (currentSide.equals(ChessPieceState.Side.BLACK)) {
        moveResult.blackCondition = ChessMoveResult.Condition.CHECK;
        moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
      } else {
        moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
        moveResult.whiteCondition = ChessMoveResult.Condition.CHECK;
      }
    } else {
      moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
      moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
    }
  }

  private void updateMoveResult() {
    List<Position> positionListOverride = new ArrayList<>();
    for (Position position : moveResult.legalPositions) {
      if (!willPutKingInCheck(internalChessPieceToMove, position)) {
        positionListOverride.add(new Position(position));
      }
    }
    checkCastlingMoveTo();
    moveResult.legalPositions = positionListOverride;
  }

  private void checkCastlingMoveTo() {
    if (!internalChessPieceToMove.type.equals(Type.KING)) {
      return;
    }
    //checking positions king is moving through and ultimately to, to make sure none of them would put him in check
    if (moveResult.queenSideCastleMove != null) {
      if (willPutKingInCheck(internalChessPieceToMove, moveResult.queenSideCastleMove.positionToMoveKingTo)) {
        moveResult.queenSideCastleMove = null;
      } else if (willPutKingInCheck(internalChessPieceToMove, new Position(GamePieceState.File.D, moveResult.queenSideCastleMove.positionToMoveKingTo.rank))) {
        moveResult.queenSideCastleMove = null;
      }
    }
    if (moveResult.kingSideCastleMove != null) {
      if (willPutKingInCheck(internalChessPieceToMove, moveResult.kingSideCastleMove.positionToMoveKingTo)) {
        moveResult.kingSideCastleMove = null;
      } else if (willPutKingInCheck(internalChessPieceToMove, new Position(GamePieceState.File.F, moveResult.kingSideCastleMove.positionToMoveKingTo.rank))) {
        moveResult.kingSideCastleMove = null;
      }
    }
  }

  private boolean checkIfKingIsInCheckmate() {
    if (isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
      if (currentSide.equals(Side.BLACK)) {
        moveResult.blackCondition = ChessMoveResult.Condition.CHECKMATE;
        moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
        moveResult.legalPositions = new ArrayList<>();
      } else {
        moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
        moveResult.whiteCondition = ChessMoveResult.Condition.CHECKMATE;
        moveResult.legalPositions = new ArrayList<>();
      }
      return true;
    }
    return false;
  }

  private boolean checkIfKingIsInStalemate() {
    if (!isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
      moveResult.blackCondition = Condition.STALEMATE;
      moveResult.whiteCondition = Condition.STALEMATE;
      moveResult.legalPositions = new ArrayList<>();
      return true;
    }
    return false;
  }

  private boolean areAnyLegalMovesForCurrentSide() {
    for (ChessPieceState chessPieceState : internalState) {
      if (chessPieceState.side.equals(currentSide)) {
        ChessMoveResult legalMovesMoveResult = getChessPieceWorker(internalState, chessPieceState).getLegalMoves();
        if (getLegalListOfPositions(chessPieceState, legalMovesMoveResult).size() > 0) {
          return true;
        }
      }
    }
    return false;
  }

  private List<Position> getLegalListOfPositions(ChessPieceState chessPieceToMove, ChessMoveResult moveResultToCheck) {
    List<Position> positionListOverride = new ArrayList<>();
    for (Position position : moveResultToCheck.legalPositions) {
      if (!willPutKingInCheck(chessPieceToMove, position)) {
        positionListOverride.add(new Position(position));
      }
    }
    return positionListOverride;
  }

  private boolean isKingInCheck() {
    for (ChessPieceState chessPieceState : internalState) {
      if (!chessPieceState.side.equals(currentSide)) {
        ChessMoveResult moveResult = getChessPieceWorker(internalState, chessPieceState).getLegalMoves();
        for (Position position : moveResult.legalPositions) {
          if (position.rank.equals(currentKingState.position.rank)
              && position.file.equals(currentKingState.position.file)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean willPutKingInCheck(ChessPieceState chessPieceToMove, Position positionToMoveCurrentPieceTo) {
    List<ChessPieceState> stateClone = cloneState(chessPieceToMove);
    ChessPieceState spoofedChessPieceState = new ChessPieceState(chessPieceToMove);
    spoofedChessPieceState.position = positionToMoveCurrentPieceTo;
    stateClone.add(spoofedChessPieceState);
    return isKingInCheckCloned(stateClone, spoofedChessPieceState);
  }

  private List<ChessPieceState> cloneState(ChessPieceState chessPieceToMove) {
    List<ChessPieceState> stateClone = new ArrayList<>();
    for (ChessPieceState pieceToClone : internalState) {
      if (pieceToClone.type.equals(chessPieceToMove.type)
          && pieceToClone.position.rank.equals(chessPieceToMove.position.rank)
          && pieceToClone.position.file.equals(chessPieceToMove.position.file)) {
        continue;
      }
      stateClone.add(new ChessPieceState(pieceToClone));
    }
    return stateClone;
  }

  private boolean isKingInCheckCloned(List<ChessPieceState> stateClone, ChessPieceState chessPieceMoving) {
    if (stateClone == null || stateClone.size() < 1) {
      return false;
    }
    ChessPieceState currentKingState = getCurrentKingStateCloned(stateClone);
    if (currentKingState == null) {
      return false;
    }
    for (ChessPieceState chessPieceState : stateClone) {
      if (!chessPieceState.side.equals(currentSide)
          && !(chessPieceState.position.rank.equals(chessPieceMoving.position.rank)
          && chessPieceState.position.file.equals(chessPieceMoving.position.file))
          ) {
        ChessMoveResult moveResult = getChessPieceWorker(stateClone, chessPieceState).getLegalMoves();
        for (Position position : moveResult.legalPositions) {
          if (position.rank.equals(currentKingState.position.rank)
              && position.file.equals(currentKingState.position.file)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private ChessPieceState getCurrentKingStateCloned(List<ChessPieceState> stateClone) {
    if (stateClone == null) {
      return null;
    }
    for (ChessPieceState currentChessPieceState : stateClone) {
      if (currentChessPieceState.side.equals(currentSide)
          && currentChessPieceState.type.equals(Type.KING)) {
        return currentChessPieceState;
      }
    }
    return null;
  }

}
