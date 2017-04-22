package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.CastleMove;
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

    private void checkCastleMoves() {
        moveResult.queenSideCastleMove = computeCastleMoveFor(0, 2, 3);
        moveResult.kingSideCastleMove = computeCastleMoveFor(7, 6, 5);
    }

    
    private CastleMove computeCastleMoveFor(int rookStart, int kingFinish, int rookFinish){
        if (!chessPieceToMove.isInitialMove) {
            return null;
        }
        
        int kingFile = chessPieceToMove.position.file.ordinal();
        
        Rank rank = chessPieceToMove.position.rank;
        ChessPieceState rook = positionArray[rank.ordinal()][rookStart];
        
        if(!rangeIsClear(rank, kingFile, rookStart)) {
            return null;
        }
        
        if(rookIncorrect(rook)) {
            return null;
        }

        return buildCastleMove(rank, rook, kingFinish, rookFinish);
    }
    
    private boolean rangeIsClear(Rank currentRank, int fromFile, int toFile) {
    	if(fromFile > toFile){
    		int toFileBackup = toFile;
    		toFile = fromFile;
    		fromFile = toFileBackup;
    	}
    	
    	for(int file = fromFile + 1; file <= toFile - 1; file++){
            Position position = new Position(File.values()[file], currentRank);
            if(!isSpaceFreeFullCheck(position)){
                return false;
            }
        }
    	return true;
    }

	private CastleMove buildCastleMove(Rank currentRank, ChessPieceState rook, int kingFile, int rookFile) {
		CastleMove kingSideCastleMove = new CastleMove();
        kingSideCastleMove.positionOfKing = chessPieceToMove.position;
        kingSideCastleMove.positionOfRook = rook.position;
        kingSideCastleMove.positionToMoveKingTo = new Position(File.values()[kingFile], currentRank);
        kingSideCastleMove.positionToMoveRookTo = new Position(File.values()[rookFile], currentRank);
		return kingSideCastleMove;
	}

	private boolean rookIncorrect(ChessPieceState rook) {
		return rook == null
                || !rook.type.equals(ChessPieceState.Type.ROOK)
                || !rook.side.equals(chessPieceToMove.side)
                || !rook.isInitialMove;
	}
}
