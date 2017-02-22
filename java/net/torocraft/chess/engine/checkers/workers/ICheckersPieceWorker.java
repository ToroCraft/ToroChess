package net.torocraft.chess.engine.checkers.workers;

import net.torocraft.chess.engine.checkers.CheckersMoveResult;

public interface ICheckersPieceWorker {
    CheckersMoveResult getLegalMoves();
}
