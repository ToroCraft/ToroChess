package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

import static net.torocraft.chess.engine.GamePieceState.Position;

public class BishopWorker extends ChessPieceWorker {
    public BishopWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        walkSouthEast();
        walkSouthWest();
        walkNorthEast();
        walkNorthWest();
        return moveResult;
    }

    private void walkSouthWest() {
        int rankInt = chessPieceToMove.position.rank.ordinal() - 1;
        int fileInt = chessPieceToMove.position.file.ordinal() - 1;
        while (rankInt >= 0 && fileInt >= 0) {
            Position position = tryCreatePosition(rankInt, fileInt);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
            rankInt--;
            fileInt--;
        }
    }

    private void walkSouthEast() {
        int rankInt = chessPieceToMove.position.rank.ordinal() - 1;
        int fileInt = chessPieceToMove.position.file.ordinal() + 1;
        while (rankInt >= 0 && fileInt < 8) {
            Position position = tryCreatePosition(rankInt, fileInt);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
            rankInt--;
            fileInt++;
        }
    }

    private void walkNorthWest() {
        int rankInt = chessPieceToMove.position.rank.ordinal() + 1;
        int fileInt = chessPieceToMove.position.file.ordinal() - 1;
        while (rankInt < 8 && fileInt >= 0) {
            Position position = tryCreatePosition(rankInt, fileInt);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
            rankInt++;
            fileInt--;
        }
    }

    private void walkNorthEast() {
        int rankInt = chessPieceToMove.position.rank.ordinal() + 1;
        int fileInt = chessPieceToMove.position.file.ordinal() + 1;
        while (rankInt >= 0 && fileInt >= 0) {
            Position position = tryCreatePosition(rankInt, fileInt);
            if (isSpaceFreeFullCheck(position)) {
                addLegalMove(position);
            } else {
                checkIfEnemy(position);
                return;
            }
            rankInt++;
            fileInt++;
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
