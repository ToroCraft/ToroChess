package net.torocraft.chess.enities;

public interface IChessPiece {
	
	public static enum Side {WHITE, BLACK};
	
	Side getSide();
	
	void setSide(Side side);
	
	
}
