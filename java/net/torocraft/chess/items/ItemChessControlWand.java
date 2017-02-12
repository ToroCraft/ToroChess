package net.torocraft.chess.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.ChessGame;

public class ItemChessControlWand extends Item {

	public static ItemChessControlWand INSTANCE;

	public static String NAME = "chess_control_wand";

	private BlockPos chessControlBlockPostion;
	private ChessGame game;

	public static void init() {
		INSTANCE = new ItemChessControlWand();
		GameRegistry.register(INSTANCE, new ResourceLocation(ToroChess.MODID, NAME));
	}

	public static void registerRenders() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0,
				new ModelResourceLocation(ToroChess.MODID + ":" + NAME, "inventory"));
	}

	public ItemChessControlWand() {
		setUnlocalizedName(NAME);
		setMaxDamage(1);
	}

	public void setChessControlBlockPosition(BlockPos pos) {
		this.chessControlBlockPostion = pos;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {

		if (!world.isRemote) {
			ChessGame game = getGame(world);

			if (game == null) {
				System.out.println("No world");

			} else if (targetedEntity == null) {
				System.out.println("no target, clicked on [" + game.getPositionName(pos) + "]");

			} else {

				String chessPosition = game.getPositionName(pos);

				targetedEntity.setChessPosition(chessPosition);

				System.out.println("Sending target to [" + chessPosition + "] Block Pos[" + pos.toString() + "]");

			}
		}

		return EnumActionResult.PASS;
	}

	private EntityChessPiece targetedEntity;

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {

		if (!(entity instanceof EntityChessPiece)) {
			return false;
		}

		ChessGame game = getGame(entity.world);
		if (game == null) {
			return true;
		}

		if (entity.world.isRemote) {
			targetedEntity = (EntityChessPiece) entity;
			System.out.println("targeted a chess pieced at [" + targetedEntity.getChessPosition() + "]");
		} else {
			targetedEntity.spawnExplosionParticle();
		}

		return true;
	}

	private ChessGame getGame(World world) {
		if (game == null) {
			try {
				game = ((BlockChessControl) world.getBlockState(chessControlBlockPostion).getBlock()).getGame();
			} catch (Exception e) {
				System.out.println("game block missing: " + e.getMessage());
			}
		}

		return game;
	}

}
