package net.torocraft.chess.gen;

import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;

/**
 * <pre>
 * <h2>Minecraft Axes</h2>
 * +----> X (east)
 * |
 * |
 * v  
 * Z (south)
 * 
 * <h2>Board Layout (chess positions)</h2>
 * a8 b8 ... h8
 * 
 * a7
 * 
 * ...
 * 
 * a1 b1 ... h1
 * 
 * <h2>Board Layout (world offsets X,Z)</h2>
 * 0,0 1,0 ... 7,0
 * 
 * 0,1
 * 
 * ...
 * 
 * 0,7 1,7 ... 7,7
 * 
 * </pre>
 */

public class CheckerBoardUtil {

	public static BlockPos toWorldCoords(BlockPos a8, Position position) {
		return a8.add(position.file.ordinal(), 0, position.rank.ordinal());
	}

	public static Position getChessPosition(BlockPos a8, BlockPos coords) {
		if(a8.getY() != coords.getY()){
			return null;
		}
		int xLocal = coords.getX() - a8.getX();
		int zLocal = coords.getZ() - a8.getZ();
		
		if(zLocal > 7 || xLocal > 7 || xLocal < 0 || zLocal < 0){
			return null;
		}
		
		return new Position(File.values()[xLocal], Rank.values()[zLocal]);
	}
}
