package slimeknights.mantle.client.model.util;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

public class BakedItemModel implements BakedModel, TransformTypeDependentItemBakedModel {
  protected final ImmutableList<BakedQuad> quads;
  protected final TextureAtlasSprite particle;
  protected final ImmutableMap<TransformType, Transformation> transforms;
  protected final ItemOverrides overrides;
  protected final BakedModel guiModel;
  protected final boolean isSideLit;

  public BakedItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, Transformation> transforms, ItemOverrides overrides, boolean untransformed, boolean isSideLit)
  {
    this.quads = quads;
    this.particle = particle;
    this.transforms = transforms;
    this.overrides = overrides;
    this.isSideLit = isSideLit;
    this.guiModel = untransformed && hasGuiIdentity(transforms) ? new BakedGuiItemModel<>(this) : null;
  }

  private static boolean hasGuiIdentity(ImmutableMap<TransformType, Transformation> transforms)
  {
    Transformation guiTransform = transforms.get(TransformType.GUI);
    return guiTransform == null || guiTransform.equals(Transformation.identity());
  }

  @Override public boolean useAmbientOcclusion() { return true; }
  @Override public boolean isGui3d() { return false; }
  @Override public boolean usesBlockLight() { return isSideLit; }
  @Override public boolean isCustomRenderer() { return false; }
  @Override public TextureAtlasSprite getParticleIcon() { return particle; }

  @Override
  public ItemTransforms getTransforms() {
    return ItemTransforms.NO_TRANSFORMS;
  }

  @Override public ItemOverrides getOverrides() { return overrides; }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
  {
    if (side == null)
    {
      return quads;
    }
    return ImmutableList.of();
  }

  @Override
  public BakedModel handlePerspective(TransformType type, PoseStack poseStack)
  {
    if (type == TransformType.GUI && this.guiModel != null)
    {
      return ((TransformTypeDependentItemBakedModel)this.guiModel).handlePerspective(type, poseStack);
    }
    return ModelHelper.handlePerspective(this, transforms, type, poseStack);
  }

  public static class BakedGuiItemModel<T extends BakedItemModel> extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel
  {
    private final ImmutableList<BakedQuad> quads;

    public BakedGuiItemModel(T originalModel)
    {
      this.wrapped = originalModel;
      ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
      for (BakedQuad quad : originalModel.quads)
      {
        if (quad.getDirection() == Direction.SOUTH)
        {
          builder.add(quad);
        }
      }
      this.quads = builder.build();
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
      if(side == null)
      {
        return quads;
      }
      return ImmutableList.of();
    }

    @Override
    public BakedModel handlePerspective(TransformType type, PoseStack poseStack)
    {
      if (type == TransformType.GUI)
      {
        return ModelHelper.handlePerspective(this, ((BakedItemModel)wrapped).transforms, type, poseStack);
      }

      if(this.wrapped instanceof TransformTypeDependentItemBakedModel model)
        return model.handlePerspective(type, poseStack);
      return this;
    }
  }
}
