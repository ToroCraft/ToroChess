package net.torocraft.chess.engine;

public interface IGamePieceWorker<Result extends GameMoveResult> {
	Result getLegalMoves();

}
