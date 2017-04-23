package net.torocraft.chess.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.control.ChessPieceSearchPredicate;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessPieceState.Type;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.bishop.EntityBishop;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.knight.EntityKnight;
import net.torocraft.chess.entities.queen.EntityQueen;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.items.ChessPieceAtPredicate;
import net.torocraft.chess.items.HighlightedChessPiecePredicate;

/**
 * <pre>
 * <h2>Minecraft Axes</h2>
 * 
 *           z
 *           ^
 *     white |a1 
 *           |
 *     black |
 * x<--------+
 *    h8      a8 (board origin)
 * 
 * </pre>
 */

public class CheckerBoardUtil {

	public static BlockPos toWorldCoords(BlockPos a8, Position position) {
		if (a8 == null) {
			throw new NullPointerException("a8 is null");
		}
		if (position == null) {
			throw new NullPointerException("position is null");
		}
		return a8.add(7 - position.file.ordinal(), 0, position.rank.ordinal());
	}

	public static Position getChessPosition(BlockPos a8, BlockPos pos) {
		if (a8 == null) {
			throw new NullPointerException("a8 is null");
		}
		if (pos == null) {
			throw new NullPointerException("position is null");
		}
		if (a8.getY() != pos.getY()) {
			return null;
		}
		int xLocal = pos.getX() - a8.getX();
		int zLocal = pos.getZ() - a8.getZ();

		if (zLocal > 7 || xLocal > 7 || xLocal < 0 || zLocal < 0) {
			return null;
		}

		return new Position(File.values()[7 - xLocal], Rank.values()[zLocal]);
	}

	public static Side castSide(Boolean side) {
		if (side != null && side) {
			return Side.BLACK;
		}
		return Side.WHITE;
	}

	public static boolean castSide(Side side) {
		if (side != null && side.equals(Side.BLACK)) {
			return true;
		}
		return false;
	}

	public static List<ChessPieceState> loadPiecesFromWorld(World world, UUID gameId, BlockPos a8) {
		List<ChessPieceState> pieces = new ArrayList<>();

		List<EntityChessPiece> entityPieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(a8.add(4, 0, 4)).expand(80, 20, 80), new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece entityPiece : entityPieces) {
			pieces.add(convertToState(entityPiece));
		}

		return pieces;
	}

	public static ChessPieceState convertToState(EntityChessPiece entity) {
		ChessPieceState state = new ChessPieceState();

		if (entity instanceof EntityBishop) {
			state.type = Type.BISHOP;

		} else if (entity instanceof EntityKing) {
			state.type = Type.KING;

		} else if (entity instanceof EntityKnight) {
			state.type = Type.KNIGHT;

		} else if (entity instanceof EntityQueen) {
			state.type = Type.QUEEN;

		} else if (entity instanceof EntityRook) {
			state.type = Type.ROOK;

		} else {
			state.type = Type.PAWN;
		}

		state.side = entity.getSide();
		state.position = entity.getChessPosition();
		state.isInitialMove = entity.isInitialMove();

		return state;
	}

	@Deprecated
	public static EntityChessPiece getHighlightedPiece(World world, Position piecePos, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.toWorldCoords(a8, piecePos)).expand(80, 20, 80), new HighlightedChessPiecePredicate(gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

	public static EntityChessPiece getPiece(World world, Position piecePos, BlockPos a8, UUID gameId) {
		if (piecePos == null) {
			throw new NullPointerException("piecePos is null");
		}
		if (a8 == null) {
			throw new NullPointerException("a8 is null");
		}
		if (gameId == null) {
			throw new NullPointerException("gameId is null");
		}

		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class,
				new AxisAlignedBB(CheckerBoardUtil.toWorldCoords(a8, piecePos)).expand(80, 20, 80), new ChessPieceAtPredicate(piecePos, gameId));

		if (pieces == null || pieces.size() < 1) {
			return null;
		}

		return pieces.get(0);
	}

}
