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
		if(a8 == null){
			throw new NullPointerException("a8 is null");
		}
		if(position == null){
			throw new NullPointerException("position is null");
		}
		return a8.add(position.file.ordinal(), 0, position.rank.ordinal());
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
		
		return new Position(File.values()[xLocal], Rank.values()[zLocal]);
	}
}
