package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.Side;

import java.util.List;

public class PawnWorker extends ChessPieceWorker {
    public PawnWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public MoveResult getLegalMoves() {
        if (chessPieceToMove.side.equals(Side.BLACK)) {
            checkForwardBlack();
            checkDiagonalsBlack();
        } else {
            checkForwardWhite();
            checkDiagonalsWhite();
        }
        return moveResult;
    }

    private void checkForwardBlack() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal());
        if(isSpaceFreeFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        } else {
            return;
        }

        if (chessPieceToMove.isInitialMove) {
            positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()-2,
                    chessPiecePosition.file.ordinal());
            if (isSpaceFreeFullCheck(positionToCheck)) {
                addLegalMove(positionToCheck);
            }
        }
    }

    private void checkDiagonalsBlack() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal()+1);
        if(isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }

        positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal()-1);
        if(isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }

    private void checkForwardWhite() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal());
        if(isSpaceFreeFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        } else {
            return;
        }

        if (chessPieceToMove.isInitialMove) {
            positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()+2,
                    chessPiecePosition.file.ordinal());
            if (isSpaceFreeFullCheck(positionToCheck)) {
                addLegalMove(positionToCheck);
            }
        }
    }

    private void checkDiagonalsWhite() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal()-1);
        if(isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }

        positionToCheck = tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal()+1);
        if(isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }
}
