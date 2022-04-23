package com.falsepattern.ssmlegacy.render;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderTileSoundMuffler extends TileEntitySpecialRenderer {
    //private static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(SuperSoundMuffler.MOD_ID + ":" + BlockSoundMuffler.NAME, "inventory");

    /**
     * Code commandeered from Botania[https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/render/tile/RenderTileFloatingFlower.java]
     * for dat sexy sinusoidal motion
     */
    private void renderTileEntityAt(TileEntitySoundMuffler tile, double x, double y, double z, float partialTicks) {
        if (tile != null) {

            // BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GL11.glPushMatrix();
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);

            double worldTime = SuperSoundMuffler.ticksInGame + partialTicks;
            worldTime += new Random(Objects.hash(tile.xCoord, tile.yCoord, tile.zCoord)).nextInt(1000);

            GL11.glRotatef(-((float) worldTime * 0.5f), 0f, 1f, 0f);
            GL11.glTranslatef(-0.1f, (float) Math.sin(worldTime * 0.05f) * 0.1f, 0.1f);

            GL11.glRotatef(4f * (float) Math.sin(worldTime * 0.04f), 1f, 0, 0);
            GL11.glScalef(1, -1, -1);

            bindTexture(ModelSoundMuffler.TEXTURE_LOCATION);
            ModelSoundMuffler.render();
            //TODO actually render the model
//            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
//            state = state.getBlock().getExtendedState(state, tile.getWorld(), tile.getPos());
//            IBakedModel model = brd.getBlockModelShapes().getModelManager().getModel(MODEL_LOCATION);
//            brd.getBlockModelRenderer().renderModelBrightness(model, state, 1.0F, true);

            GL11.glPopMatrix();
        }
    }
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile instanceof TileEntitySoundMuffler) {
            renderTileEntityAt((TileEntitySoundMuffler) tile, x, y, z, partialTicks);
        }
    }
}
