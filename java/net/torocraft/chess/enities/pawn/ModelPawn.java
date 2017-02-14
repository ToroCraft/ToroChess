package net.torocraft.chess.enities.pawn;

import net.minecraft.client.model.ModelBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPawn extends ModelBiped {
	public ModelPawn() {
		this(0.0F, false);
	}

	public ModelPawn(float modelSize, boolean p_i1168_2_) {
		super(modelSize, 0.0F, 64, p_i1168_2_ ? 32 : 64);
	}

}