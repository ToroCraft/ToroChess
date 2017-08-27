package net.torocraft.chess.engine.chess.workers;

import static net.torocraft.chess.engine.chess.ChessPieceState.File;
import static net.torocraft.chess.engine.chess.ChessPieceState.Position;
import static net.torocraft.chess.engine.chess.ChessPieceState.Rank;

import java.util.ArrayList;
import java.util.List;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;

public abstract class ChessPieceWorker implements IChessPieceWorker {

  protected final List<ChessPieceState> state;
  protected final ChessPieceState chessPieceToMove;
  protected ChessMoveResult moveResult;
  protected ChessPieceState[][] positionArray;
  private List<ChessPieceState> stateClone;

  public ChessPieceWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
    this.state = state;
    this.chessPieceToMove = chessPieceToMove;
    moveResult = new ChessMoveResult();
    moveResult.legalPositions = new ArrayList<>();
    positionArray = new ChessPieceState[8][8];
    populatePositionArray();
  }

  private void populatePositionArray() {
    for (ChessPieceState piece : state) {
      positionArray[piece.position.rank.ordinal()][piece.position.file.ordinal()]
          = piece;
    }
  }

  protected boolean isSpaceFree(Position position) {
    if (position == null || !(position.rank.ordinal() >= 0 && position.rank.ordinal() < 8)) {
      return false;
    }
    return positionArray[position.rank.ordinal()][position.file.ordinal()] == null;
  }

  protected boolean isEnemyOccupying(Position position) {
    if (position == null || !(position.file.ordinal() >= 0 && position.file.ordinal() < 8)) {
      return false;
    }
    ChessPieceState pieceState = positionArray[position.rank.ordinal()][position.file.ordinal()];
    return pieceState != null
        && pieceState.side != null
        && !pieceState.side.equals(chessPieceToMove.side);
  }

  protected boolean isSpaceFreeFullCheck(Position positionToCheck) {
    return positionToCheck != null && isSpaceFree(positionToCheck);
  }

  protected boolean isEnemyOccupyingFullCheck(Position positionToCheck) {
    return positionToCheck != null && isEnemyOccupying(positionToCheck);
  }

  protected void addLegalMove(Position position) {
    if (position == null) {
      return;
    }
    moveResult.legalPositions.add(position);
  }

  protected Position tryCreatePosition(int rank, int file) {
    if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
      return new Position(File.values()[file],
          Rank.values()[rank]);
    }
    return null;
  }
}
