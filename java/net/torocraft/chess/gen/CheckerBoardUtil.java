package net.torocraft.chess.gen;

import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;

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
		if(a8 == null){
			throw new NullPointerException("a8 is null");
		}
		if(position == null){
			throw new NullPointerException("position is null");
		}
		return a8.add(7 - position.file.ordinal(), 0, position.rank.ordinal());
	}

	public static Position getChessPosition(BlockPos a8, BlockPos pos) {
		if(a8 == null){
			throw new NullPointerException("a8 is null");
		}
		if(pos == null){
			throw new NullPointerException("position is null");
		}
		if(a8.getY() != pos.getY()){
			return null;
		}
		int xLocal = pos.getX() - a8.getX();
		int zLocal = pos.getZ() - a8.getZ();
		
		if(zLocal > 7 || xLocal > 7 || xLocal < 0 || zLocal < 0){
			return null;
		}
		
		return new Position(File.values()[7 - xLocal], Rank.values()[zLocal]);
	}
}
