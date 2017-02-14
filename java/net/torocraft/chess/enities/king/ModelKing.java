package net.torocraft.chess.enities.king;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelKing extends ModelBiped {
	public ModelKing() {
		this(0.0F, 0.0F, false);
	}

	public ModelKing(float p_i1165_1_, float p_i1165_2_, boolean p_i1165_3_) {
		super(p_i1165_1_, 0.0F, 64, p_i1165_3_ ? 32 : 64);

		if (p_i1165_3_) {
			this.bipedHead = new ModelRenderer(this, 0, 0);
			this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, p_i1165_1_);
			this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1165_2_, 0.0F);
			this.bipedBody = new ModelRenderer(this, 16, 16);
			this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1165_2_, 0.0F);
			this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i1165_1_ + 0.1F);
			this.bipedRightLeg = new ModelRenderer(this, 0, 16);
			this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + p_i1165_2_, 0.0F);
			this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1165_1_ + 0.1F);
			this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
			this.bipedLeftLeg.mirror = true;
			this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + p_i1165_2_, 0.0F);
			this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1165_1_ + 0.1F);
		} else {
			this.bipedHead = new ModelRenderer(this, 0, 0);
			this.bipedHead.setRotationPoint(0.0F, p_i1165_2_, 0.0F);
			this.bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, p_i1165_1_);
			this.bipedHead.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, p_i1165_1_);
			this.bipedBody = new ModelRenderer(this, 16, 20);
			this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1165_2_, 0.0F);
			this.bipedBody.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, p_i1165_1_);
			this.bipedBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, p_i1165_1_ + 0.05F);
			this.bipedRightArm = new ModelRenderer(this, 44, 38);
			this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i1165_1_);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1165_2_, 0.0F);
			this.bipedLeftArm = new ModelRenderer(this, 44, 38);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, p_i1165_1_);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1165_2_, 0.0F);
			this.bipedRightLeg = new ModelRenderer(this, 0, 22);
			this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + p_i1165_2_, 0.0F);
			this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1165_1_);
			this.bipedLeftLeg = new ModelRenderer(this, 0, 22);
			this.bipedLeftLeg.mirror = true;
			this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + p_i1165_2_, 0.0F);
			this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1165_1_);
		}
	}

}