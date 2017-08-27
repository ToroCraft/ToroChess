package net.torocraft.chess.engine.chess.workers;

import static net.torocraft.chess.engine.GamePieceState.Position;

import java.util.List;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;

public class KnightWorker extends ChessPieceWorker {

  public KnightWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
    super(state, chessPieceToMove);
  }

  @Override
  public ChessMoveResult getLegalMoves() {
    Position chessPiecePosition = chessPieceToMove.position;

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() - 2,
        chessPiecePosition.file.ordinal() - 1));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() - 2,
        chessPiecePosition.file.ordinal() + 1));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() + 1,
        chessPiecePosition.file.ordinal() - 2));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() + 1,
        chessPiecePosition.file.ordinal() + 2));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() - 1,
        chessPiecePosition.file.ordinal() + 2));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() - 1,
        chessPiecePosition.file.ordinal() - 2));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() + 2,
        chessPiecePosition.file.ordinal() - 1));

    checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal() + 2,
        chessPiecePosition.file.ordinal() + 1));

    return moveResult;
  }

  private void checkIfLegalMove(Position positionToCheck) {
    if (positionToCheck == null) {
      return;
    }
    if (isSpaceFreeFullCheck(positionToCheck) || isEnemyOccupyingFullCheck(positionToCheck)) {
      addLegalMove(positionToCheck);
    }
  }
}
