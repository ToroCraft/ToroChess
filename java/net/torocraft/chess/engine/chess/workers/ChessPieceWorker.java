package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import static net.torocraft.chess.engine.chess.ChessPieceState.File;
import static net.torocraft.chess.engine.chess.ChessPieceState.Position;
import static net.torocraft.chess.engine.chess.ChessPieceState.Rank;
import static net.torocraft.chess.engine.chess.ChessPieceState.Type;

import java.util.ArrayList;
import java.util.List;

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
        return positionToCheck != null
                && isSpaceFree(positionToCheck)
                && !willPutKingInCheck(positionToCheck);
    }

    protected boolean isEnemyOccupyingFullCheck(Position positionToCheck) {
        return positionToCheck != null
                && isEnemyOccupying(positionToCheck)
                && !willPutKingInCheck(positionToCheck);
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

    @Override
    public boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo) {
        cloneState();
        ChessPieceState spoofedChessPieceState = new ChessPieceState(chessPieceToMove);

        spoofedChessPieceState.position = positionToMoveCurrentPieceTo;
        stateClone.add(spoofedChessPieceState);
        return isKingInCheck();
    }

    private void cloneState() {
        stateClone = new ArrayList<>();
        for (ChessPieceState pieceToClone : state) {
            if (pieceToClone.type.equals(chessPieceToMove.type)
                    && pieceToClone.position.rank.equals(chessPieceToMove.position.rank)
                    && pieceToClone.position.file.equals(chessPieceToMove.position.file)) {
                continue;
            }
            stateClone.add(new ChessPieceState(pieceToClone));
        }
    }

    private boolean isKingInCheck() {
        if (stateClone == null || stateClone.size() < 1) {
            return false;
        }
        ChessPieceState currentKingState = getCurrentKingState();
        if (currentKingState == null) {
            return false;
        }
        //TODO pretend king is a queen, and a knight, and step outwards
        //TODO until he hits the pieces, and if any are enemy and are a type that can eat him, then in check
        return false;
    }

    private ChessPieceState getCurrentKingState() {
        if (stateClone == null) {
            return null;
        }
        for (ChessPieceState currentChessPieceState : stateClone) {
            if (currentChessPieceState.side.equals(chessPieceToMove.side)
                    && currentChessPieceState.type.equals(Type.KING)) {
                return currentChessPieceState;
            }
        }
        return null;
    }
}
