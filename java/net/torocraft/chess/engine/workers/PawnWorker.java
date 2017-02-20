package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import static net.torocraft.chess.engine.ChessPieceState.Position;
import static net.torocraft.chess.engine.ChessPieceState.Side;
import static net.torocraft.chess.engine.ChessPieceState.Rank;
import static net.torocraft.chess.engine.ChessPieceState.File;

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
        Position positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()],
                Rank.values()[chessPiecePosition.rank.ordinal()-1]);
        if(isSpaceFreePawn(positionToCheck)){
            addLegalMove(positionToCheck);
        } else {
            return;
        }

        if (chessPieceToMove.isInitialMove) {
            positionToCheck =
                    new Position(File.values()[chessPiecePosition.file.ordinal()],
                            Rank.values()[chessPiecePosition.rank.ordinal()-2]);
            if (isSpaceFreePawn(positionToCheck)) {
                addLegalMove(positionToCheck);
            }
        }
    }

    private void checkDiagonalsBlack() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()-1],
                        Rank.values()[chessPiecePosition.rank.ordinal()-1]);
        if(isEnemyOccupyingPawn(positionToCheck)){
            addLegalMove(positionToCheck);
        }

        positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()-1],
                        Rank.values()[chessPiecePosition.rank.ordinal()+1]);
        if(isEnemyOccupyingPawn(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }

    private void checkForwardWhite() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()],
                        Rank.values()[chessPiecePosition.rank.ordinal()+1]);
        if(isSpaceFreePawn(positionToCheck)){
            addLegalMove(positionToCheck);
        } else {
            return;
        }

        if (chessPieceToMove.isInitialMove) {
            positionToCheck =
                    new Position(File.values()[chessPiecePosition.file.ordinal()],
                            Rank.values()[chessPiecePosition.rank.ordinal()+2]);
            if (isSpaceFreePawn(positionToCheck)) {
                addLegalMove(positionToCheck);
            }
        }
    }

    private void checkDiagonalsWhite() {
        Position chessPiecePosition = chessPieceToMove.position;
        Position positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()+1],
                        Rank.values()[chessPiecePosition.rank.ordinal()-1]);
        if(isEnemyOccupyingPawn(positionToCheck)){
            addLegalMove(positionToCheck);
        }

        positionToCheck =
                new Position(File.values()[chessPiecePosition.file.ordinal()+1],
                        Rank.values()[chessPiecePosition.rank.ordinal()+1]);
        if(isEnemyOccupyingPawn(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }

    private boolean isSpaceFreePawn(Position positionToCheck) {
        return isSpaceFree(positionToCheck) && !willPutKingInCheck(positionToCheck);
    }

    private boolean isEnemyOccupyingPawn(Position positionToCheck) {
        return isEnemyOccupying(positionToCheck) && !willPutKingInCheck(positionToCheck);
    }
}
