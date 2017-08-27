package net.torocraft.chess.engine.chess.workers;

import java.util.List;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;

public class BishopWorker extends ChessPieceWorker {

  public BishopWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
    super(state, chessPieceToMove);
  }

  @Override
  public ChessMoveResult getLegalMoves() {
    DiagonalChessPieceWorker diagonalWorker = new DiagonalChessPieceWorker(state, chessPieceToMove);
    moveResult = diagonalWorker.getLegalMoves();
    return moveResult;
  }
}
