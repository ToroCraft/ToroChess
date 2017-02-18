package net.torocraft.chess.enities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;

public class EntityAIMoveToPosition extends EntityAIBase {
	private static final double SPEED = 0.5;
	
	private final EntityChessPiece entity;
	private int timeoutCounter;
	private String chessPosition;
	private BlockPos destination;
	private double targetX;
	private double targetY;
	private double targetZ;
	private boolean isAboveDestination;
	private boolean moving = false;
	
	
	
	public EntityAIMoveToPosition(EntityChessPiece creature) {
		this.entity = creature;
		this.setMutexBits(3);
	}

	private void determineDestination() {
		destination = CheckerBoardUtil.toWorldCoords(entity.getA8(), entity.getChessPosition());
		targetX = (double) this.destination.getX() + 0.5d;
		targetY = (double) this.destination.getY();
		targetZ = (double) this.destination.getZ() + 0.5d;
	}

	@Override
	public boolean shouldExecute() {
		if (entity.hasMoved()) {
			entity.resetMovedFlag();
			isAboveDestination = false;
			moving = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean continueExecuting() {
		return !isAboveDestination;
	}

	@Override
	public void updateTask() {

		updateDestination();

		if (destination == null) {
			isAboveDestination = true;
			timeoutCounter = 0;
			return;
		}

		double distance = distanceFromDestination();

		if (distance <= 0.02) {
			isAboveDestination = true;
			timeoutCounter = 0;
			return;
		}

		if (distance < 1) {
			entity.setPosition(targetX, targetY, targetZ);
			timeoutCounter = 0;
			isAboveDestination = true;
			return;
		}

		timeoutCounter++;
		if (moving && timeoutCounter < 40) {
			return;
		}

		
		moving = entity.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, SPEED);
		timeoutCounter = 0;
	}

	private void updateDestination() {
		if (entity.getChessPosition() == null) {
			destination = null;
			return;
		}

		if (entity.getChessPosition().equals(chessPosition)) {
			return;
		}

		determineDestination();
	}

	private double distanceFromXDesination() {
		return entity.posX - targetX;
	}

	private double distanceFromZDesination() {
		return entity.posZ - targetZ;
	}

	private double distanceFromDestination() {
		double x = distanceFromXDesination();
		double z = distanceFromZDesination();
		return Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
	}

	protected boolean getIsAboveDestination() {
		return this.isAboveDestination;
	}

}