package com.falsepattern.ssmlegacy.block;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Random;

public class RenderTileSoundMuffler extends TileEntitySpecialRenderer {
    //private static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(SuperSoundMuffler.MOD_ID + ":" + BlockSoundMuffler.NAME, "inventory");

    /**
     * Code commandeered from Botania[https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/render/tile/RenderTileFloatingFlower.java]
     * for dat sexy sinusoidal motion
     */
    private void renderTileEntityAt(TileEntitySoundMuffler tile, double x, double y, double z, float partialTicks) {
        if (tile != null) {
            if (!tile.getWorldObj().blockExists(tile.xCoord, tile.yCoord, tile.zCoord)) {
                return;
            }

            // BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GL11.glPushMatrix();
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glTranslated(x, y, z);

            double worldTime = SuperSoundMuffler.ticksInGame + partialTicks;
            worldTime += new Random(Objects.hash(tile.xCoord, tile.yCoord, tile.zCoord)).nextInt(1000);

            GL11.glTranslatef(0.5f, 0, 0.5f);
            GL11.glRotatef(-((float) worldTime * 0.5f), 0f, 1f, 0f);
            GL11.glTranslatef(-0.5f, (float) Math.sin(worldTime * 0.05f) * 0.5f, 0.5f);

            GL11.glRotatef(4f * (float) Math.sin(worldTime * 0.04f), 1f, 0, 0);

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

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
