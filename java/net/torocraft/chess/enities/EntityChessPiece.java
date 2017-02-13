package net.torocraft.chess.enities;

import java.util.UUID;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.enities.ai.EntityAILookDownBoard;
import net.torocraft.chess.enities.ai.EntityAIMoveToPosition;

public abstract class EntityChessPiece extends EntityCreature implements IChessPiece {

	private static final String NBT_SIDE_KEY = "chessside";
	private static final String NBT_POSITION_KEY = "chessposition";
	private static final String NBT_A1_POSITION_KEY = "a1position";
	private static final String NBT_GAME_ID_KEY = "gameid";

	private Side side = Side.WHITE;
	private String chessPosition;
	private BlockPos a1Pos;
	private UUID gameId;
	private boolean moved = true;
	double x = 0;
	double z = 0;

	public EntityChessPiece(World worldIn) {
		super(worldIn);
		experienceValue = 0;
		setHealth(10f);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIMoveToPosition(this));
		this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F));
		this.tasks.addTask(6, new EntityAILookDownBoard(this));
	}

	public void resetMovedFlag() {
		moved = false;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public boolean hasMoved() {
		return moved;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (x != posX || z != posZ) {
			moved = true;
		}
		x = posX;
		z = posZ;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.getEntity() == null) {
			return false;
		}
		if (source.getEntity() instanceof EntityChessPiece) {
			setDead();
		} else {
			source.getEntity().attackEntityFrom(source, amount);
		}
		return false;
	}

	@Override
	public boolean getCanSpawnHere() {
		return false;
	}

	@Override
	protected boolean canDropLoot() {
		return false;
	}

	private boolean isMissingValues() {
		if (side == null || chessPosition == null || a1Pos == null || gameId == null) {
			setDead();
			return true;
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		if (isMissingValues()) {
			return;
		}
		super.writeEntityToNBT(tagCompound);
		tagCompound.setBoolean(NBT_SIDE_KEY, castSide(side));
		tagCompound.setString(NBT_POSITION_KEY, chessPosition);
		tagCompound.setLong(NBT_A1_POSITION_KEY, a1Pos.toLong());
		tagCompound.setUniqueId(NBT_GAME_ID_KEY, gameId);
	}

	private boolean castSide(Side side) {
		if (Side.BLACK.equals(side)) {
			return true;
		} else {
			return false;
		}
	}

	private Side castSide(Boolean side) {
		if (side != null && side) {
			return Side.BLACK;
		} else {
			return Side.WHITE;
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);

		try {
			side = castSide(tagCompund.getBoolean(NBT_SIDE_KEY));
			chessPosition = tagCompund.getString(NBT_POSITION_KEY);
			a1Pos = BlockPos.fromLong(tagCompund.getLong(NBT_A1_POSITION_KEY));
			gameId = tagCompund.getUniqueId(NBT_GAME_ID_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!isMissingValues()) {
			return;
		}
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public void setSide(Side side) {
		this.side = side;
	}

	@Override
	public void setChessPosition(String position) {
		moved = true;
		chessPosition = position;
	}

	@Override
	public String getChessPosition() {
		return chessPosition;
	}

	@Override
	public BlockPos getA1Pos() {
		return a1Pos;
	}

	@Override
	public void setA1Pos(BlockPos a1Pos) {
		this.a1Pos = a1Pos;
	}

	@Override
	public UUID getGameId() {
		return gameId;
	}

	@Override
	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

}
