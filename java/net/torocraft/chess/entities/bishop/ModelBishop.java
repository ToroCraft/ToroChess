package net.torocraft.chess.entities.bishop;

import net.minecraft.client.model.ModelWitch;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBishop extends ModelWitch {

  public ModelBishop(float scale) {
    super(scale);
  }
}