package net.torocraft.chess.entities.queen;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelQueen extends ModelBiped {

  public ModelQueen() {
    super(0f, 0.0F, 64, 32);
    float modelSize = 0f;
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
    this.bipedLeftArm = new ModelRenderer(this, 40, 16);
    this.bipedLeftArm.mirror = true;
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
    this.bipedRightLeg = new ModelRenderer(this, 0, 16);
    this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
    this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
    this.bipedLeftLeg.mirror = true;
    this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
  }

  @Override
  public void postRenderArm(float scale, EnumHandSide side) {
    float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
    ModelRenderer modelrenderer = getArmForSide(side);
    modelrenderer.rotationPointX += f;
    modelrenderer.postRender(scale);
    modelrenderer.rotationPointX -= f;
  }
}