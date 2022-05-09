package rosegoldclient.utils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlurUtils {

    private static HashMap<Float, OutputStuff> blurOutput;
    private static HashMap<Float, Long> lastBlurUse;
    private static HashSet<Float> requestedBlurs;
    private static int fogColour;
    private static Framebuffer fb;
    private static Framebuffer blurOutputHorz;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenRender(RenderGameOverlayEvent.Pre event) {
        processBlurs();
        Main.mc.getFramebuffer().bindFramebuffer(false);
    }

    public static void processBlurs() {
        final long currentTime = System.currentTimeMillis();
        for (final float blur : BlurUtils.requestedBlurs) {
            BlurUtils.lastBlurUse.put(blur, currentTime);
            final int width = Main.mc.displayWidth;
            final int height = Main.mc.displayHeight;
            final OutputStuff output = BlurUtils.blurOutput.computeIfAbsent(blur, k -> {
                fb = new Framebuffer(width, height, false);
                fb.setFramebufferFilter(9728);
                return new OutputStuff(fb, null, null);
            });
            if (output.framebuffer.framebufferWidth != width || output.framebuffer.framebufferHeight != height) {
                output.framebuffer.createBindFramebuffer(width, height);
                if (output.blurShaderHorz != null) {
                    output.blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
                }
                if (output.blurShaderVert != null) {
                    output.blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
                }
            }
            blurBackground(output, blur);
        }
        final Set<Float> remove = new HashSet<>();
        for (final Map.Entry<Float, Long> entry : BlurUtils.lastBlurUse.entrySet()) {
            if (currentTime - entry.getValue() > 30000L) {
                remove.add(entry.getKey());
            }
        }
        for (final Map.Entry<Float, OutputStuff> entry2 : BlurUtils.blurOutput.entrySet()) {
            if (remove.contains(entry2.getKey())) {
                entry2.getValue().framebuffer.deleteFramebuffer();
                entry2.getValue().blurShaderHorz.deleteShader();
                entry2.getValue().blurShaderVert.deleteShader();
            }
        }
        BlurUtils.lastBlurUse.keySet().removeAll(remove);
        BlurUtils.blurOutput.keySet().removeAll(remove);
        BlurUtils.requestedBlurs.clear();
    }

    private static Matrix4f createProjectionMatrix(final int width, final int height) {
        final Matrix4f projMatrix = new Matrix4f();
        projMatrix.setIdentity();
        projMatrix.m00 = 2.0f / width;
        projMatrix.m11 = 2.0f / -height;
        projMatrix.m22 = -0.0020001999f;
        projMatrix.m33 = 1.0f;
        projMatrix.m03 = -1.0f;
        projMatrix.m13 = 1.0f;
        projMatrix.m23 = -1.0001999f;
        return projMatrix;
    }

    private static void blurBackground(final OutputStuff output, final float blurFactor) {
        if (!OpenGlHelper.isFramebufferEnabled() || !OpenGlHelper.areShadersSupported()) {
            return;
        }
        final int width = Main.mc.displayWidth;
        final int height = Main.mc.displayHeight;
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        if (BlurUtils.blurOutputHorz == null) {
            (BlurUtils.blurOutputHorz = new Framebuffer(width, height, false)).setFramebufferFilter(9728);
        }
        if (BlurUtils.blurOutputHorz == null || output == null) {
            return;
        }
        if (BlurUtils.blurOutputHorz.framebufferWidth != width || BlurUtils.blurOutputHorz.framebufferHeight != height) {
            BlurUtils.blurOutputHorz.createBindFramebuffer(width, height);
            Main.mc.getFramebuffer().bindFramebuffer(false);
        }
        if (output.blurShaderHorz == null) {
            try {
                output.blurShaderHorz = new Shader(Main.mc.getResourceManager(), "blur", output.framebuffer, BlurUtils.blurOutputHorz);
                output.blurShaderHorz.getShaderManager().getShaderUniform("BlurDir").set(1.0f, 0.0f);
                output.blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
            }
            catch (Exception ignored) {}
        }
        if (output.blurShaderVert == null) {
            try {
                output.blurShaderVert = new Shader(Main.mc.getResourceManager(), "blur", BlurUtils.blurOutputHorz, output.framebuffer);
                output.blurShaderVert.getShaderManager().getShaderUniform("BlurDir").set(0.0f, 1.0f);
                output.blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
            }
            catch (Exception ignored) {}
        }
        if (output.blurShaderHorz != null && output.blurShaderVert != null) {
            if (output.blurShaderHorz.getShaderManager().getShaderUniform("Radius") == null) {
                return;
            }
            output.blurShaderHorz.getShaderManager().getShaderUniform("Radius").set(blurFactor);
            output.blurShaderVert.getShaderManager().getShaderUniform("Radius").set(blurFactor);
            GL11.glPushMatrix();
            GL30.glBindFramebuffer(36008, Main.mc.getFramebuffer().framebufferObject);
            GL30.glBindFramebuffer(36009, output.framebuffer.framebufferObject);
            GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.framebuffer.framebufferWidth, output.framebuffer.framebufferHeight, 16384, 9728);
            output.blurShaderHorz.render(0.0f);
            output.blurShaderVert.render(0.0f);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();
        }
        Main.mc.getFramebuffer().bindFramebuffer(false);
    }

    public static void renderBlurredBackground(final float blurStrength, final int screenWidth, final int screenHeight, final float x, final float y, final float blurWidth, final float blurHeight) {
        if (!OpenGlHelper.isFramebufferEnabled() || !OpenGlHelper.areShadersSupported()) {
            return;
        }
        if (blurStrength < 0.5) {
            return;
        }
        BlurUtils.requestedBlurs.add(blurStrength);
        if (BlurUtils.blurOutput.isEmpty()) {
            return;
        }
        OutputStuff out = BlurUtils.blurOutput.get(blurStrength);
        if (out == null) {
            out = BlurUtils.blurOutput.values().iterator().next();
        }
        final float uMin = x / screenWidth;
        final float uMax = (x + blurWidth) / screenWidth;
        final float vMin = (screenHeight - y) / screenHeight;
        final float vMax = (screenHeight - y - blurHeight) / screenHeight;
        GlStateManager.depthMask(false);
        RenderUtils.drawRect(x, y, x + blurWidth, y + blurHeight, BlurUtils.fogColour);
        out.framebuffer.bindFramebufferTexture();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax, 9728);
        out.framebuffer.unbindFramebufferTexture();
        GlStateManager.depthMask(true);
        GlStateManager.resetColor();
    }

    public static void drawTexturedRect(final float x, final float y, final float width, final float height, final float uMin, final float uMax, final float vMin, final float vMax, final int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(770, 771, 1, 771);
        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);
        GlStateManager.disableBlend();
    }

    public static void drawTexturedRectNoBlend(final float x, final float y, final float width, final float height, final float uMin, final float uMax, final float vMin, final float vMax, final int filter) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(3553, 10241, filter);
        GL11.glTexParameteri(3553, 10240, filter);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x, y + height, 0.0).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(x + width, y + height, 0.0).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(x + width, y, 0.0).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(x, y, 0.0).tex(uMin, vMin).endVertex();
        tessellator.draw();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
    }

    static {
        BlurUtils.blurOutput = new HashMap<>();
        BlurUtils.lastBlurUse = new HashMap<>();
        BlurUtils.requestedBlurs = new HashSet<>();
        BlurUtils.fogColour = 0;
        BlurUtils.blurOutputHorz = null;
    }

    private static class OutputStuff
    {
        public Framebuffer framebuffer;
        public Shader blurShaderHorz;
        public Shader blurShaderVert;

        public OutputStuff(final Framebuffer framebuffer, final Shader blurShaderHorz, final Shader blurShaderVert) {
            this.framebuffer = framebuffer;
            this.blurShaderHorz = blurShaderHorz;
            this.blurShaderVert = blurShaderVert;
        }
    }
}
