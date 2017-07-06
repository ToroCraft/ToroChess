package net.torocraft.chess.items;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.MessageLegalMovesRequest;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.items.extendedreach.IExtendedReach;

@Mod.EventBusSubscriber
public class ItemChessControlWand extends Item implements IExtendedReach {

  public static final float REACH_DISTANCE = 40;

  public static final String NBT_A8_POS = "chessa8";
  public static final String NBT_CONTROL_POS = "chesscontrol";
  public static final String NBT_GAME_ID = "chessgameid";
  public static final String NAME = "chess_control_wand";

  public static ItemChessControlWand INSTANCE_BLACK;
  public static ItemChessControlWand INSTANCE_WHITE;

  private final Side side;

  public ItemChessControlWand(Side side) {
    setUnlocalizedName(NAME);
    setMaxDamage(1);
    setMaxStackSize(1);
    this.side = side;
  }

  @SubscribeEvent
  public static void init(Register<Item> event) {
    INSTANCE_BLACK = new ItemChessControlWand(Side.BLACK);
    INSTANCE_WHITE = new ItemChessControlWand(Side.WHITE);
    event.getRegistry().register(INSTANCE_BLACK.setRegistryName(new ResourceLocation(ToroChess.MODID, NAME + "_black")));
    event.getRegistry().register(INSTANCE_WHITE.setRegistryName(new ResourceLocation(ToroChess.MODID, NAME + "_white")));
  }

  @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
  public static void registerRenders() {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    renderItem.getItemModelMesher().register(INSTANCE_BLACK, 0, new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_black", "inventory"));
    renderItem.getItemModelMesher().register(INSTANCE_WHITE, 0, new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_white", "inventory"));
  }

  public static TileEntityChessControl getChessControlAt(World world, ItemStack wand) {

    BlockPos a8 = getA8(wand);
    BlockPos lastKnownControlPos = getChessControlPos(wand);
    UUID gameId = getGameId(wand);

    TileEntityChessControl control = getChessControlAt(world, lastKnownControlPos, gameId);

    if (control != null) {
      return control;
    }

    control = searchForChessControl(world, a8, gameId);

    if (control != null) {
      wand.getTagCompound().setLong(ItemChessControlWand.NBT_CONTROL_POS, control.getPos().toLong());
    }

    return control;
  }

  private static TileEntityChessControl searchForChessControl(World world, BlockPos a8, UUID gameId) {
    BlockPos searchCenter = a8.subtract(BlockChessControl.A8_CENTER_OFFSET);

    int SEARCH_RADIUS = 40;
    TileEntityChessControl control = null;

    for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
      for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
        for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
          control = getChessControlAt(world, searchCenter.add(x, y, z), gameId);
          if (control != null) {
            return control;
          }
        }
      }
    }

    return control;
  }

  public static TileEntityChessControl getChessControlAt(World world, BlockPos pos) {
    if (world.getBlockState(pos).getBlock() != BlockChessControl.INSTANCE) {
      return null;
    }

    TileEntity te = world.getTileEntity(pos);

    if (te == null || !(te instanceof TileEntityChessControl)) {
      return null;
    }

    return (TileEntityChessControl) te;
  }

  public static TileEntityChessControl getChessControlAt(World world, BlockPos pos, UUID gameId) {

    TileEntityChessControl control = getChessControlAt(world, pos);

    if (control == null || control.getGameId() == null || !control.getGameId().equals(gameId)) {
      return null;
    }

    return control;
  }

  public static BlockPos getChessControlPos(ItemStack stack) {
    return BlockPos.fromLong(stack.getTagCompound().getLong(ItemChessControlWand.NBT_CONTROL_POS));
  }

  public static BlockPos getA8(ItemStack stack) {
    return BlockPos.fromLong(stack.getTagCompound().getLong(ItemChessControlWand.NBT_A8_POS));
  }

  public static UUID getGameId(ItemStack stack) {
    if (stack == null || stack.isEmpty() || stack.getTagCompound() == null) {
      return null;
    }
    return stack.getTagCompound().getUniqueId(ItemChessControlWand.NBT_GAME_ID);
  }

  @Override
  public EnumActionResult onItemUseExtended(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX,
      float hitY, float hitZ) {

    ItemStack wand = player.getHeldItem(hand);

    if (wand == null || !wand.hasTagCompound() || !(wand.getItem() instanceof ItemChessControlWand)) {
      return EnumActionResult.PASS;
    }

    BlockPos a8 = getA8(wand);
    TileEntityChessControl control = getChessControlAt(world, wand);

    if (world.isRemote) {
      return EnumActionResult.PASS;
    }

    if (control == null) {
      return EnumActionResult.PASS;
    }

    Position to = CheckerBoardUtil.getChessPosition(a8, pos);

    if (control.movePiece(a8, to)) {
      yupSound(player);
    } else {
      nopeSound(player);
    }

    return EnumActionResult.SUCCESS;
  }

  @Override
  public boolean itemInteractionForEntityExtended(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
    if (!(target instanceof EntityChessPiece)) {
      return false;
    }

    ItemStack wand = player.getHeldItem(hand);

    if (wand == null || !(wand.getItem() instanceof ItemChessControlWand)) {
      return false;
    }

    EntityChessPiece piece = (EntityChessPiece) target;
    BlockPos a8 = getA8(wand);

    TileEntityChessControl control = getChessControlAt(player.world, wand);

    if (player.world.isRemote) {
      ToroChess.NETWORK.sendToServer(new MessageLegalMovesRequest(getChessControlPos(wand)));
      return false;
    }

    if (control == null) {
      return false;
    }

    if (player.isSneaking()) {
      if (control.castlePiece(a8, piece.getChessPosition())) {
        yupSound(player);
      } else {
        nopeSound(player);
      }
      return true;
    }

    if (canAttack(wand, piece)) {
      if (control.movePiece(a8, piece.getChessPosition())) {
        yupSound(player);
      } else {
        nopeSound(player);
      }
      return true;
    }

    if (canSelect(wand, piece)) {
      if (control.selectEntity(piece)) {
        selectSound(player);
      } else {
        nopeSound(player);
      }
      return true;
    }

    return false;
  }

  private void nopeSound(EntityPlayer player) {
    player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.NEUTRAL, 1f,
        1f);
  }

  private void yupSound(EntityPlayer player) {
    player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1f,
        1f);
  }

  private void selectSound(EntityPlayer player) {
    player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
        SoundCategory.NEUTRAL, 0.5f, 1f);
  }

  private boolean canSelect(ItemStack wand, EntityChessPiece piece) {
    if (piece == null) {
      return false;
    }
    return side.equals(piece.getSide()) && getGameId(wand).equals(piece.getGameId());
  }

  private boolean canAttack(ItemStack wand, EntityChessPiece target) {
    if (target == null) {
      return false;
    }
    return side.equals(target.getSide()) && getGameId(wand).equals(target.getGameId());
  }

  public Side getSide() {
    return side;
  }

  @Override
  public float getReach() {
    return REACH_DISTANCE;
  }

  @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_A8_POS)) {
      tooltip.add("Already Placed:");
      tooltip.add(BlockPos.fromLong(stack.getTagCompound().getLong(NBT_A8_POS)).toString());
      tooltip.add(stack.getTagCompound().getUniqueId(NBT_GAME_ID).toString());
    }
  }

}
