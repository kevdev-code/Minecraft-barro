package io.github.kevdev_code.barro.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;

import io.github.kevdev_code.barro.BarroContent;

public final class BarroFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // The seat is invisible, so vanilla's empty renderer is all it needs.
        EntityRenderers.register(BarroContent.ASIENTO.get(), NoopRenderer::new);
    }
}
