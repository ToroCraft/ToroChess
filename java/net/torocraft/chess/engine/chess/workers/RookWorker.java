package net.torocraft.chess.engine.chess.workers;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

import static net.torocraft.chess.engine.GamePieceState.Position;
import static net.torocraft.chess.engine.GamePieceState.Rank;
import static net.torocraft.chess.engine.GamePieceState.File;

public class RookWorker extends ChessPieceWorker {
    public RookWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        walkNorth();
        walkSouth();
        walkWest();
        walkEast();
        return moveResult;
    }

    private void walkNorth() {
        File file = chessPieceToMove.position.file;
        Rank rank = chessPieceToMove.position.rank;
        for (int i = file.ordinal() - 1; i >= 0; i--) {
            Position position = tryCreatePosition(rank.ordinal(), i);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
        }
    }

    private void walkSouth() {
        File file = chessPieceToMove.position.file;
        Rank rank = chessPieceToMove.position.rank;
        for (int i = file.ordinal() + 1; i < 8; i++) {
            Position position = tryCreatePosition(rank.ordinal(), i);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
        }
    }

    private void walkWest() {
        Rank rank = chessPieceToMove.position.rank;
        File file = chessPieceToMove.position.file;
        for (int i = rank.ordinal() - 1; i >= 0; i--) {
            Position position = tryCreatePosition(i, file.ordinal());
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
        }
    }

    private void walkEast() {
        Rank rank = chessPieceToMove.position.rank;
        File file = chessPieceToMove.position.file;
        for (int i = rank.ordinal() + 1; i < 8; i++) {
            Position position = tryCreatePosition(i, file.ordinal());
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
        }
    }

    private void checkIfEnemy(Position positionToCheck) {
        if (positionToCheck == null) {
            return;
        }
        if(isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }
}
