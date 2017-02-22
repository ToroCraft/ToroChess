package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import static net.torocraft.chess.engine.chess.ChessPieceState.File;
import static net.torocraft.chess.engine.chess.ChessPieceState.Position;
import static net.torocraft.chess.engine.chess.ChessPieceState.Rank;

import java.util.List;

public class KingWorker extends ChessPieceWorker {
    public KingWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        Position chessPiecePosition = chessPieceToMove.position;
        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal()-1));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal()));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()-1,
                chessPiecePosition.file.ordinal()+1));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal(),
                chessPiecePosition.file.ordinal()-1));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal(),
                chessPiecePosition.file.ordinal()+1));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal()-1));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal()));

        checkIfLegalMove(tryCreatePosition(chessPiecePosition.rank.ordinal()+1,
                chessPiecePosition.file.ordinal()+1));

        checkCastleMoves();

        return moveResult;
    }

    private void checkIfLegalMove(Position positionToCheck) {
        if (positionToCheck == null) {
            return;
        }
        if(isSpaceFreeFullCheck(positionToCheck) || isEnemyOccupyingFullCheck(positionToCheck)){
            addLegalMove(positionToCheck);
        }
    }

    private void checkCastleMoves(){
        if (willPutKingInCheck(chessPieceToMove.position)) {
            return;
        }
        checkIfCanCastleOnQueenSide();
        checkIfCanCastleOnKingSide();
    }

    private void checkIfCanCastleOnQueenSide() {
        if (!chessPieceToMove.isInitialMove) {
            return;
        }
        Rank currentRank = chessPieceToMove.position.rank;
        for(int file = 3; file>0; file--){
            Position position = new Position(File.values()[file], currentRank);
            if(!isSpaceFreeFullCheck(position)){
                return;
            }
        }

        ChessPieceState pieceInRookCastlingPosition = positionArray[currentRank.ordinal()][7];
        if (pieceInRookCastlingPosition == null
            || !pieceInRookCastlingPosition.type.equals(ChessPieceState.Type.ROOK)
            || !pieceInRookCastlingPosition.side.equals(chessPieceToMove.side)
            || !pieceInRookCastlingPosition.isInitialMove) {
            return;
        }

        addLegalMove(new Position(File.values()[2],currentRank));
    }

    private void checkIfCanCastleOnKingSide(){
        if (!chessPieceToMove.isInitialMove) {
            return;
        }
        Rank currentRank = chessPieceToMove.position.rank;
        for(int file = 5; file<7; file++){
            Position position = new Position(File.values()[file], currentRank);
            if(!isSpaceFreeFullCheck(position)){
                return;
            }
        }

        ChessPieceState pieceInRookCastlingPosition = positionArray[currentRank.ordinal()][0];
        if (pieceInRookCastlingPosition == null
                || !pieceInRookCastlingPosition.type.equals(ChessPieceState.Type.ROOK)
                || !pieceInRookCastlingPosition.side.equals(chessPieceToMove.side)
                || !pieceInRookCastlingPosition.isInitialMove) {
            return;
        }

        addLegalMove(new Position(File.values()[6],currentRank));
    }
}
