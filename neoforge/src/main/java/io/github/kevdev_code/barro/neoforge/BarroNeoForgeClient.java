package io.github.kevdev_code.barro.neoforge;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import io.github.kevdev_code.barro.BarroContent;

// Client-only setup for NeoForge. The seat is invisible, so vanilla's empty renderer is all it needs.
final class BarroNeoForgeClient {
    private BarroNeoForgeClient() {
    }

    static void register(IEventBus modBus) {
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class,
                event -> event.registerEntityRenderer(BarroContent.ASIENTO.get(), NoopRenderer::new));
    }
}
