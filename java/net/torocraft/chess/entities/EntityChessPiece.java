package net.torocraft.chess.entities;

import com.google.common.base.Predicate;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.chess.ToroChessEvent.MoveEvent;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.entities.ai.EntityAILookDownBoard;
import net.torocraft.chess.entities.ai.EntityAIMoveToPosition;

public abstract class EntityChessPiece extends EntityCreature implements IChessPiece {

  private static final String NBT_SIDE_KEY = "chessside";
  private static final String NBT_POSITION_FILE_KEY = "chess_file_position";
  private static final String NBT_POSITION_RANK_KEY = "chess_rank_position";
  private static final String NBT_A8_POSITION_KEY = "a8position";
  private static final String NBT_GAME_ID_KEY = "gameid";
  private static final String NBT_INITIAL_MOVE = "chess_initial_move";

  private static final DataParameter<Boolean> SIDE_IS_WHITE = EntityDataManager.<Boolean>createKey(EntityZombieVillager.class,
      DataSerializers.BOOLEAN);
  double x = 0;
  double z = 0;
  boolean initialMove = true;
  boolean moveInProgress = true;
  int gameOverCountdown = 0;
  private Position chessPosition;
  private Position prevChessPosition;
  private BlockPos a8;
  private UUID gameId;
  private boolean moved = true;
  private boolean clearCondition = false;

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
  public Side getSide() {
    return castSide(dataManager.get(SIDE_IS_WHITE));
  }

  @Override
  public void setSide(Side side) {
    dataManager.set(SIDE_IS_WHITE, castSide(side));
  }

  @Override
  protected void initEntityAI() {
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
    if (clearCondition) {
      return true;
    }

    if (source.getEntity() == null || !(source.getEntity() instanceof EntityChessPiece)) {
      return false;
    }

    EntityChessPiece attacker = (EntityChessPiece) source.getEntity();
    return isEnemy(attacker);
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

    c.setInteger(NBT_POSITION_FILE_KEY, chessPosition.file.ordinal());
    c.setInteger(NBT_POSITION_RANK_KEY, chessPosition.rank.ordinal());

    c.setLong(NBT_A8_POSITION_KEY, a8.toLong());
    c.setUniqueId(NBT_GAME_ID_KEY, gameId);
    c.setBoolean(NBT_INITIAL_MOVE, initialMove);
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
      File letter = File.values()[c.getInteger(NBT_POSITION_FILE_KEY)];
      Rank number = Rank.values()[c.getInteger(NBT_POSITION_RANK_KEY)];
      chessPosition = new Position(letter, number);

      a8 = BlockPos.fromLong(c.getLong(NBT_A8_POSITION_KEY));
      gameId = c.getUniqueId(NBT_GAME_ID_KEY);
      dataManager.set(SIDE_IS_WHITE, c.getBoolean(NBT_SIDE_KEY));
      initialMove = c.getBoolean(NBT_INITIAL_MOVE);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!isMissingValues()) {
      return;
    }
  }

  public void setAttackAllMode() {
    Predicate<EntityChessPiece> isOtherSide = new Predicate<EntityChessPiece>() {
      @Override
      public boolean apply(EntityChessPiece e) {
        return isEnemy(e);
      }
    };

    this.targetTasks.addTask(1,
        new EntityAINearestAttackableTarget<EntityChessPiece>(this, EntityChessPiece.class, 2, false, false, isOtherSide));

    clearCondition = true;
  }

  public boolean isEnemy(EntityChessPiece e) {
    if (e == null || e.getGameId() == null || e.getSide() == null) {
      return false;
    }
    return !getSide().equals(e.getSide()) && e.getGameId().equals(gameId);
  }

  public void setClearCondition() {
    clearCondition = true;
  }

  public void onMoveComplete() {
    moveInProgress = false;
    if (prevChessPosition == null) {
      return;
    }
    MinecraftForge.EVENT_BUS.post(new MoveEvent.Finish(world, gameId, this, prevChessPosition, chessPosition));
  }

  @Override
  public Position getChessPosition() {
    return chessPosition;
  }

  @Override
  public void setChessPosition(Position position) {
    moved = true;
    prevChessPosition = chessPosition;
    chessPosition = position;
    initialMove = false;
    if (prevChessPosition == null) {
      return;
    }
    moveInProgress = true;
    MinecraftForge.EVENT_BUS.post(new MoveEvent.Start(world, gameId, this, prevChessPosition, chessPosition));
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

  @Override
  public boolean isInitialMove() {
    return initialMove;
  }

  @Override
  public void setInitialMove(boolean initialMove) {
    this.initialMove = initialMove;
  }

  public SoundCategory getSoundCategory() {
    return SoundCategory.HOSTILE;
  }

  protected SoundEvent getSwimSound() {
    return SoundEvents.ENTITY_HOSTILE_SWIM;
  }

  protected SoundEvent getSplashSound() {
    return SoundEvents.ENTITY_HOSTILE_SPLASH;
  }

  protected SoundEvent getHurtSound() {
    return SoundEvents.ENTITY_ZOMBIE_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_ZOMBIE_DEATH;
  }

  protected SoundEvent getStepSound() {
    return SoundEvents.ENTITY_ZOMBIE_STEP;
  }

  protected SoundEvent getFallSound(int heightIn) {
    return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
  }

  public boolean isMoveInProgress() {
    return moveInProgress;
  }

}