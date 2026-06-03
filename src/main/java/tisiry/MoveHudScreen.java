package tisiry;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MoveHudScreen extends Screen {
    private static final Identifier TEXTURE_PANEL = new Identifier("decor", "textures/keyi1.png");

    private final Screen parent;
    private int activeElement = -1; // 0:XYZ, 1:FPS, 2:Ping, 3:TPS
    private long lastClickTime = 0;
    private int lastClickedElement = -1;

    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    public MoveHudScreen(Screen parent) {
        super(Text.literal("VupsPing"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = this.textRenderer;

       
        drawCenteredString(matrices, renderer, "VupsPing — Настройка интерфейса", this.width / 2, 10, 0xFFAA00);

        float scale = ModConfig.hudScale * 1.4f;

        if (activeElement != -1) {
            int newX = (int) (mouseX - dragOffsetX);
            int newY = (int) (mouseY - dragOffsetY);

            if (activeElement == 0) { ModConfig.xyzX = newX; ModConfig.xyzY = newY; }
            if (activeElement == 1) { ModConfig.fpsX = newX; ModConfig.fpsY = newY; }
            if (activeElement == 2) { ModConfig.pingX = newX; ModConfig.pingY = newY; }
            if (activeElement == 3) { ModConfig.tpsX = newX; ModConfig.tpsY = newY; }
        }

        String[] texts = new String[]{
                String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()),
                "FPS: " + HudData.getFps(client) + " ↑↓",
                "Ping: " + HudData.getPing(client) + " ms",
                "TPS: 20"
        };

        boolean[] enabledStates = new boolean[]{ModConfig.showXyz, ModConfig.showFps, ModConfig.showPing, ModConfig.showTps};
        int[] xs = new int[]{ModConfig.xyzX, ModConfig.fpsX, ModConfig.pingX, ModConfig.tpsX};
        int[] ys = new int[]{ModConfig.xyzY, ModConfig.fpsY, ModConfig.pingY, ModConfig.tpsY};

        int padding = 6;
        int height = 16;

        for (int i = 0; i < 4; i++) {
            int w = renderer.getWidth(texts[i]);

            matrices.push();
            matrices.translate(xs[i], ys[i], 0);
            matrices.scale(scale, scale, 1.0f);

            int x1 = -padding;
            int x2 = w + padding;
            int y1 = -4;
            int y2 = height - 4;

            drawScaledTexturePanel(matrices, x1, y1, x2, y2);

            int textColor = enabledStates[i] ? 0xFFFFFF : 0x44FFFFFF;
            renderer.drawWithShadow(matrices, texts[i], 0, 0, textColor);

            matrices.pop();
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_W) {
            ModConfig.hudScale = Math.min(2.0f, ModConfig.hudScale + 0.05f);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_S) {
            ModConfig.hudScale = Math.max(0.5f, ModConfig.hudScale - 0.05f);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    private void drawScaledTexturePanel(MatrixStack matrices, int x1, int y1, int x2, int y2) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE_PANEL);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int width = x2 - x1;
        int height = y2 - y1;

        int texW = 32;
        int texH = 16;
        int r = 4;

        drawTexture(matrices, x1, y1, 0, 0, r, height, texW, texH);

        int centerWidth = width - r * 2;
        int centerX = x1 + r;
        if (centerWidth > 0) {
            drawTexture(matrices, centerX, y1, centerWidth, height, r, 0, 1, texH, texW, texH);
        }

        drawTexture(matrices, x2 - r, y1, texW - r, 0, r, height, texW, texH);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            TextRenderer renderer = this.textRenderer;
            MinecraftClient client = MinecraftClient.getInstance();
            float scale = ModConfig.hudScale * 1.4f;

            int clicked = -1;
            int padding = 6;
            int height = 16;

            String[] texts = new String[]{
                    String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()),
                    "FPS: " + HudData.getFps(client) + " ↑↓",
                    "Ping: " + HudData.getPing(client) + " ms",
                    "TPS: 20"
            };

            int[] xs = new int[]{ModConfig.xyzX, ModConfig.fpsX, ModConfig.pingX, ModConfig.tpsX};
            int[] ys = new int[]{ModConfig.xyzY, ModConfig.fpsY, ModConfig.pingY, ModConfig.tpsY};

            for (int i = 0; i < 4; i++) {
                int w = renderer.getWidth(texts[i]);

                int x1 = xs[i] - (int)(padding * scale);
                int x2 = xs[i] + (int)((w + padding) * scale);
                int y1 = ys[i] - (int)(4 * scale);
                int y2 = ys[i] + (int)((height - 4) * scale);

                if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
                    clicked = i;
                    dragOffsetX = mouseX - xs[i];
                    dragOffsetY = mouseY - ys[i];
                    break;
                }
            }

            if (clicked != -1) {
                long currentTime = System.currentTimeMillis();
                if (clicked == lastClickedElement && (currentTime - lastClickTime) < 250) {
                    if (clicked == 0) ModConfig.showXyz = !ModConfig.showXyz;
                    if (clicked == 1) ModConfig.showFps = !ModConfig.showFps;
                    if (clicked == 2) ModConfig.showPing = !ModConfig.showPing;
                    if (clicked == 3) ModConfig.showTps = !ModConfig.showTps;
                    activeElement = -1;
                } else {
                    activeElement = clicked;
                }
                lastClickTime = currentTime;
                lastClickedElement = clicked;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        TextRenderer renderer = this.textRenderer;
        MinecraftClient client = MinecraftClient.getInstance();
        float scale = ModConfig.hudScale * 1.4f;

        int padding = 6;
        int height = 16;
        String[] texts = new String[]{
                String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()),
                "FPS: " + HudData.getFps(client) + " ↑↓",
                "Ping: " + HudData.getPing(client) + " ms",
                "TPS: 20"
        };
        int[] xs = new int[]{ModConfig.xyzX, ModConfig.fpsX, ModConfig.pingX, ModConfig.tpsX};
        int[] ys = new int[]{ModConfig.xyzY, ModConfig.fpsY, ModConfig.pingY, ModConfig.tpsY};

        boolean hovered = false;
        for (int i = 0; i < 4; i++) {
            int w = renderer.getWidth(texts[i]);
            int x1 = xs[i] - (int)(padding * scale);
            int x2 = xs[i] + (int)((w + padding) * scale);
            int y1 = ys[i] - (int)(4 * scale);
            int y2 = ys[i] + (int)((height - 4) * scale);

            if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
                hovered = true;
                break;
            }
        }

        if (hovered) {
            if (amount > 0) {
                ModConfig.hudScale = Math.min(2.0f, ModConfig.hudScale + 0.1f);
            } else {
                ModConfig.hudScale = Math.max(0.5f, ModConfig.hudScale - 0.1f);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && activeElement != -1) {
            activeElement = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        ModConfig.save();
        activeElement = -1;
        if (this.client != null) this.client.setScreen(this.parent);
    }

    private void drawCenteredString(MatrixStack matrices, TextRenderer renderer, String s, int x, int y, int color) {
        renderer.drawWithShadow(matrices, s, x - renderer.getWidth(s) / 2f, y, color);
    }
}
