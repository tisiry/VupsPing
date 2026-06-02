package tisiry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class HudData {
    public static int lastFps = 0;
    public static long lastFpsCheckTime = 0;
    public static int fpsTrend = 0;

    public static int getFps(MinecraftClient client) {
        int fps = 0;
        try {
            String[] split = client.fpsDebugString.split(" ");
            if (split.length > 0) fps = Integer.parseInt(split[0]);
        } catch (Exception ignored) {}

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFpsCheckTime > 500) {
            if (fps > lastFps) fpsTrend = 1;
            else if (fps < lastFps) fpsTrend = 2;
            else fpsTrend = 0;
            lastFps = fps;
            lastFpsCheckTime = currentTime;
        }
        return fps;
    }

    public static int getPing(MinecraftClient client) {
        if (client.getNetworkHandler() != null && client.player != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) return entry.getLatency();
        }
        return 0;
    }
    public static int getAdaptiveColor(MinecraftClient client) {
        if (client.world == null || client.player == null) return 0xFFFFFF;

        BlockPos targetPos = client.player.getBlockPos();
        HitResult hit = client.crosshairTarget;

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            targetPos = ((BlockHitResult) hit).getBlockPos();
        } else {
            targetPos = client.player.getBlockPos().up(5);
        }
        int blockLight = client.world.getLightLevel(LightType.BLOCK, targetPos);
        int skyLight = client.world.getLightLevel(LightType.SKY, targetPos);
        int currentLight = Math.max(blockLight, skyLight);
        return (currentLight > 8) ? 0x222222 : 0xFFFFFF;
    }

}