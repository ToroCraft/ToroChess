package net.torocraft.chess.enities;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.enities.ai.EntityAILookDownBoard;
import net.torocraft.chess.enities.ai.EntityAIMoveToPosition;

public abstract class EntityChessPiece extends EntityCreature implements IChessPiece {

	private static final String NBT_SIDE_KEY = "chessside";
	private static final String NBT_POSITION_LETTER_KEY = "chess_letter_position";
	private static final String NBT_POSITION_NUMBER_KEY = "chess_number_position";
	private static final String NBT_A8_POSITION_KEY = "a8position";
	private static final String NBT_GAME_ID_KEY = "gameid";

	private static final DataParameter<Boolean> SIDE_IS_WHITE = EntityDataManager.<Boolean> createKey(EntityZombieVillager.class,
			DataSerializers.BOOLEAN);

	private Position chessPosition;
	private BlockPos a8;
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
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SIDE_IS_WHITE, Boolean.valueOf(true));
	}

	@Override
	public void setSide(Side side) {
		dataManager.set(SIDE_IS_WHITE, castSide(side));
	}

	@Override
	public Side getSide() {
		return castSide(dataManager.get(SIDE_IS_WHITE));
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIAttackMelee(this, 0.5D, true));
		tasks.addTask(4, new EntityAIMoveToPosition(this));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F));
		tasks.addTask(6, new EntityAILookDownBoard(this));
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
	public boolean attackEntityAsMob(Entity entityIn) {
		if (!(entityIn instanceof EntityChessPiece)) {
			return false;
		}
		float attackDamage = 4 + entityIn.world.rand.nextInt(4);
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (canBeAttackedBy(source)) {
			return super.attackEntityFrom(source, amount);
		}
		return false;
	}

	private boolean canBeAttackedBy(DamageSource source) {
		if (source.getEntity() == null || !(source.getEntity() instanceof EntityChessPiece)) {
			return false;
		}
		Side attackerSide = ((EntityChessPiece) source.getEntity()).getSide();
		return !attackerSide.equals(getSide());
	}

	@Override
	public void onLivingUpdate() {
		this.updateArmSwingProgress();
		super.onLivingUpdate();
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
		if (chessPosition == null || a8 == null || gameId == null) {
			setDead();
			return true;
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound c) {
		if (isMissingValues()) {
			return;
		}
		super.writeEntityToNBT(c);
		c.setBoolean(NBT_SIDE_KEY, dataManager.get(SIDE_IS_WHITE));
		
		c.setInteger(NBT_POSITION_LETTER_KEY, chessPosition.letter.ordinal());
		c.setInteger(NBT_POSITION_LETTER_KEY, chessPosition.number.ordinal());
		
		c.setLong(NBT_A8_POSITION_KEY, a8.toLong());
		c.setUniqueId(NBT_GAME_ID_KEY, gameId);
	}

	private Boolean castSide(Side side) {
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
	public void readEntityFromNBT(NBTTagCompound c) {
		super.readEntityFromNBT(c);

		try {
			File letter = File.values()[c.getInteger(NBT_POSITION_LETTER_KEY)];
			Rank number = Rank.values()[c.getInteger(NBT_POSITION_NUMBER_KEY)];
			chessPosition = new Position(letter, number);
			
			a8 = BlockPos.fromLong(c.getLong(NBT_A8_POSITION_KEY));
			gameId = c.getUniqueId(NBT_GAME_ID_KEY);
			dataManager.set(SIDE_IS_WHITE, c.getBoolean(NBT_SIDE_KEY));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!isMissingValues()) {
			return;
		}
	}

	@Override
	public void setChessPosition(Position position) {
		moved = true;
		chessPosition = position;
	}

	@Override
	public Position getChessPosition() {
		return chessPosition;
	}

	@Override
	public BlockPos getA8() {
		return a8;
	}

	@Override
	public void setA8(BlockPos a8) {
		this.a8 = a8;
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
