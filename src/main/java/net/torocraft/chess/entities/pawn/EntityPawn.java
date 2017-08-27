package net.torocraft.chess.entities.pawn;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.IChessPiece;
import net.torocraft.chess.entities.queen.EntityQueen;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.gen.ChessGameGenerator;

public class EntityPawn extends EntityChessPiece implements IChessPiece {

  public static String NAME = ToroChess.MODID + "_pawn";

  public EntityPawn(World worldIn) {
    super(worldIn);
    super.setSize(0.4F, 1.1F);

  }

  public static void init(int entityId) {
    EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityPawn.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
        true);
  }

  public static void registerRenders() {
    RenderingRegistry.registerEntityRenderingHandler(EntityPawn.class, new IRenderFactory<EntityPawn>() {
      @Override
      public Render<EntityPawn> createRenderFor(RenderManager manager) {
        return new RenderPawn(manager);
      }
    });
  }

  @Override
  public boolean isChild() {
    return true;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return null;
  }

  @Override
  public float getEyeHeight() {
    float f = 1.74F;

    if (this.isChild()) {
      f = (float) ((double) f - 0.81D);
    }

    return f;
  }

  @Override
  public void onMoveComplete() {
    super.onMoveComplete();
    checkPawnConverion();
  }

  private void checkPawnConverion() {
    if (Side.WHITE.equals(getSide()) && Rank.EIGHT.equals(getChessPosition().rank)) {
      convertToQueen();
    } else if (Side.BLACK.equals(getSide()) && Rank.ONE.equals(getChessPosition().rank)) {
      convertToQueen();
    }
  }

  private void convertToQueen() {
    world.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 1f, 1f);
    EntityQueen queen = new EntityQueen(world);
    ChessGameGenerator.setGameDataToEntity(world, getA8(), getGameId(), queen, getSide(), getChessPosition().file, getChessPosition().rank);
    BlockPos pos = CheckerBoardUtil.toWorldCoords(getA8(), getChessPosition());
    queen.setPosition(pos.getX() + 0.5, getA8().getY() + 1, pos.getZ() + 0.5);
    setDead();
    world.spawnEntity(queen);
  }
}
