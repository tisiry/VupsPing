package tisiry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

public class HudRenderer {
    private static final Identifier TEXTURE_PANEL = new Identifier("decor", "textures/keyi1.png");

    public static void register() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden || client.currentScreen != null || client.player == null || client.world == null) return;

            TextRenderer renderer = client.textRenderer;
            int baseColor = 0xFFFFFF;
            float finalScale = ModConfig.hudScale * 1.4f;
            String[] texts = new String[]{
                    String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()),
                    "FPS: " + HudData.getFps(client) + " ↑↓",
                    "Ping: " + HudData.getPing(client) + " ms",
                    "TPS: 20"
            };
            boolean[] enabled = new boolean[]{ModConfig.showXyz, ModConfig.showFps, ModConfig.showPing, ModConfig.showTps};
            int[] xs = new int[]{ModConfig.xyzX, ModConfig.fpsX, ModConfig.pingX, ModConfig.tpsX};
            int[] ys = new int[]{ModConfig.xyzY, ModConfig.fpsY, ModConfig.pingY, ModConfig.tpsY};
            int padding = 6;
            int height = 16;
            for (int i = 0; i < 4; i++) {
                if (!enabled[i]) continue;
                matrixStack.push();
                matrixStack.translate(xs[i], ys[i], 0);
                matrixStack.scale(finalScale, finalScale, 1.0f);

                int x1 = -padding;
                int x2 = renderer.getWidth(texts[i]) + padding;
                int y1 = -4;
                int y2 = height - 4;

                drawTexturePanel(matrixStack, x1, y1, x2, y2);

                if (i == 1) {
                    renderer.drawWithShadow(matrixStack, "FPS: " + HudData.lastFps + " ", 0, 0, baseColor);
                    int upW = renderer.getWidth("FPS: " + HudData.lastFps + " ");
                    int upColor = (HudData.fpsTrend == 1) ? 0x00FF00 : baseColor;
                    int downColor = (HudData.fpsTrend == 2) ? 0xFF0000 : baseColor;
                    renderer.drawWithShadow(matrixStack, "↑", upW, 0, upColor);
                    renderer.drawWithShadow(matrixStack, "↓", upW + renderer.getWidth("↑"), 0, downColor);
                } else if (i == 2) {
                    renderer.drawWithShadow(matrixStack, "Ping: ", 0, 0, baseColor);
                    int pingW = renderer.getWidth("Ping: ");
                    int p = HudData.getPing(client);
                    int pColor = (p <= 50) ? 0x00FF00 : (p <= 80) ? 0xADFF2F : (p <= 100) ? 0xFFA500 : (p <= 150) ? 0x8B0000 : 0xFF0000;
                    renderer.drawWithShadow(matrixStack, p + " ms", pingW, 0, pColor);
                } else if (i == 3) {
                    renderer.drawWithShadow(matrixStack, "TPS: ", 0, 0, baseColor);
                    renderer.drawWithShadow(matrixStack, "20", renderer.getWidth("TPS: "), 0, 0x00FF00);
                } else {
                    renderer.drawWithShadow(matrixStack, texts[i], 0, 0, baseColor);
                }

                matrixStack.pop();
            }
        });
    }
    private static void drawTexturePanel(MatrixStack matrices, int x1, int y1, int x2, int y2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE_PANEL);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int width = x2 - x1;
        int height = y2 - y1;

        int texW = 32;
        int texH = 16;
        int r = 4;

        DrawableHelper.drawTexture(matrices, x1, y1, 0, 0, r, height, texW, texH);

        int centerWidth = width - r * 2;
        int centerX = x1 + r;
        if (centerWidth > 0) {
            DrawableHelper.drawTexture(matrices, centerX, y1, centerWidth, height, r, 0, 1, texH, texW, texH);
        }

        DrawableHelper.drawTexture(matrices, x2 - r, y1, texW - r, 0, r, height, texW, texH);

        RenderSystem.disableBlend();
    }
}
