package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import java.util.List;

import static net.torocraft.chess.engine.ChessPieceState.Position;

public abstract class ChessPieceWorker implements IChessPieceWorker {
    protected final List<ChessPieceState> state;
    protected final ChessPieceState chessPieceToMove;
    protected MoveResult moveResult;
    protected ChessPieceState[][] positionArray;

    public ChessPieceWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        this.state = state;
        this.chessPieceToMove = chessPieceToMove;
        moveResult = new MoveResult();
        positionArray = new ChessPieceState[8][8];
        populatePositionArray();
    }

    @Override
    public boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo) {
        //TODO, check updated state for king in check
        return false;
    }

    private void populatePositionArray() {
        for (ChessPieceState piece : state) {
            positionArray[piece.position.rank.ordinal()][piece.position.file.ordinal()]
                    = piece;
        }
    }

    protected boolean isSpaceFree(Position position) {
        return positionArray[position.rank.ordinal()][position.file.ordinal()]
                == null;
    }

    protected boolean isEnemyOccupying(Position position) {
        ChessPieceState pieceState = positionArray[position.rank.ordinal()][position.file.ordinal()];
        return !(pieceState == null || !pieceState.side.equals(chessPieceToMove.side));
    }

    protected void addLegalMove(Position position) {
        moveResult.legalPositions.add(position);
    }
}
