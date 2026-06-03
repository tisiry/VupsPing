package tisiry;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MoveHudScreen extends Screen {
    private static final Identifier TEXTURE_PANEL = Identifier.of("decor", "textures/keyi1.png");

    private final Screen parent;
    private int activeElement = -1;
    private long lastClickTime = 0;
    private int lastClickedElement = -1;

    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    public MoveHudScreen(Screen parent) {
        super(Text.literal("VupsPing"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0x3F101010, 0x3F101010);
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = this.textRenderer;

        drawCenteredString(context, renderer, "VupsPing — Настройка интерфейса", this.width / 2, 10, 0xFFAA00);

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
                client.player != null ? String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()) : "XYZ: 0, 0, 0",
                "FPS: " + client.getCurrentFps() + " ↑↓",
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

            context.getMatrices().push();
            context.getMatrices().translate(xs[i], ys[i], 0);
            context.getMatrices().scale(scale, scale, 1.0f);

            int x1 = -padding;
            int x2 = w + padding;
            int y1 = -4;
            int y2 = height - 4;

            drawScaledTexturePanel(context, x1, y1, x2, y2);

            int textColor = enabledStates[i] ? 0xFFFFFF : 0x44FFFFFF;
            context.drawTextWithShadow(renderer, texts[i], 0, 0, textColor);

            context.getMatrices().pop();
        }

        super.render(context, mouseX, mouseY, delta);
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
    private void drawScaledTexturePanel(DrawContext context, int x1, int y1, int x2, int y2) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int width = x2 - x1;
        int height = y2 - y1;
        int texW = 32;
        int texH = 16;
        int r = 4;

        context.drawTexture(TEXTURE_PANEL, x1, y1, 0, 0, r, height, texW, texH);

        int centerWidth = width - r * 2;
        int centerX = x1 + r;
        if (centerWidth > 0) {
            context.drawTexture(TEXTURE_PANEL, centerX, y1, centerWidth, height, r, 0, 1, texH, texW, texH);
        }

        context.drawTexture(TEXTURE_PANEL, x2 - r, y1, texW - r, 0, r, height, texW, texH);

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
                    client.player != null ? String.format("XYZ: %d, %d, %d", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ()) : "XYZ: 0, 0, 0",
                    "FPS: " + client.getCurrentFps() + " ↑↓",
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

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    private void drawCenteredString(DrawContext context, TextRenderer renderer, String s, int x, int y, int color) {
        context.drawTextWithShadow(renderer, s, (int)(x - renderer.getWidth(s) / 2f), y, color);
    }
}
