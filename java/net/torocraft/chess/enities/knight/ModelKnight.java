package net.torocraft.chess.enities.knight;

import net.minecraft.client.model.ModelBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelKnight extends ModelBiped {

	public ModelKnight() {
		this(0.0F, false);
	}

	public ModelKnight(float modelSize, boolean p_i1168_2_) {
		super(modelSize, 0.0F, 64, p_i1168_2_ ? 32 : 64);
	}

}