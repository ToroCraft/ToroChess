package net.torocraft.chess.enities.knight;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.IChessPiece;

public class EntityKnight extends EntityChessPiece implements IChessPiece {
	public static String NAME = "knight";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityKnight.class, NAME, entityId, ToroChess.INSTANCE, 60, 2,
				true);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityKnight.class, new IRenderFactory<EntityKnight>() {
			@Override
			public Render<EntityKnight> createRenderFor(RenderManager manager) {
				return new RenderKnight(manager);
			}
		});
	}

	public EntityKnight(World worldIn) {
		super(worldIn);
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		//setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}
	
}
