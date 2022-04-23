package com.falsepattern.ssmlegacy.render;

import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ModelSoundMuffler extends ModelBase {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Tags.MODID, "textures/block/sound_muffler.png");
    private static final ModelSoundMuffler instance = new ModelSoundMuffler();
    private static final TileEntitySoundMuffler te = new TileEntitySoundMuffler();
    public ModelRenderer renderer;

    private ModelSoundMuffler() {
        renderer = new ModelRenderer(this, 0, 0).setTextureSize(32, 16);
        //Box
        renderer.addBox(-4, -4, -4, 8, 8, 8);

        //Note Head
        renderer.addBox(-2.5f, -7, -0.5f, 2, 2, 1);

        //Note Column
        renderer.setTextureOffset(24, 0);
        renderer.addBox(-0.5f, -12, -0.5f, 1, 7, 1);

        //Note tail 1
        renderer.setTextureOffset(0, 3);
        renderer.addBox(0.5f, -11, -0.5f, 1, 1, 1);

        //Note tail 2
        renderer.setTextureOffset(4, 3);
        renderer.addBox(1.5f, -10, -0.5f, 1, 1, 1);
    }

    public static void render() {
        instance.renderer.render(0.0625f);
    }

    public static void renderInventory() {
        TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0, 0, 0, 0);
    }
}
