package io.github.kevdev_code.barro.neoforge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import io.github.kevdev_code.barro.Barro;

@Mod(Barro.MOD_ID)
public final class BarroNeoForge {
    public BarroNeoForge(IEventBus modBus) {
        // Run our common setup.
        Barro.init();
        // The seat entity needs a renderer registered, and Architectury has no registry for that, so each
        // loader does it its own way. Touching the client class only on the client keeps it off a server.
        if (Platform.getEnvironment() == Env.CLIENT) BarroNeoForgeClient.register(modBus);
    }
}
