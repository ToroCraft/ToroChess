package net.torocraft.chess.entities.king;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.IChessPiece;

public class EntityKing extends EntityChessPiece implements IChessPiece {
	public static String NAME = ToroChess.MODID + "_king";

	private static final String NBT_IN_CHECK_KEY = "inCheck";

	private static final DataParameter<Boolean> IN_CHECK = EntityDataManager.<Boolean> createKey(EntityZombieVillager.class, DataSerializers.BOOLEAN);

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityKing.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IN_CHECK, Boolean.valueOf(false));
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityKing.class, new IRenderFactory<EntityKing>() {
			@Override
			public Render<EntityKing> createRenderFor(RenderManager manager) {
				return new RenderKing(manager);
			}
		});
	}

	public EntityKing(World worldIn) {
		super(worldIn);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	public boolean isInCheck() {
		return ((Boolean) getDataManager().get(IN_CHECK)).booleanValue();
	}

	public void setInCheck(boolean inCheck) {
		if(inCheck == isInCheck()){
			return;
		}
		getDataManager().set(IN_CHECK, Boolean.valueOf(inCheck));
		if (inCheck) {
			world.setEntityState(this, (byte) 16);
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 16) {
			if (!isSilent()) {
				world.playSound(posX + 0.5D, posY + 0.5D, posZ + 0.5D, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, getSoundCategory(),
						1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound c) {
		super.writeEntityToNBT(c);
		c.setBoolean(NBT_IN_CHECK_KEY, dataManager.get(IN_CHECK));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound c) {
		super.readEntityFromNBT(c);
		try {
			dataManager.set(IN_CHECK, c.getBoolean(NBT_IN_CHECK_KEY));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
