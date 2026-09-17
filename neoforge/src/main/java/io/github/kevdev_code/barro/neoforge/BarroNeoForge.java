package io.github.kevdev_code.barro.neoforge;

import net.neoforged.fml.common.Mod;

import io.github.kevdev_code.barro.Barro;

@Mod(Barro.MOD_ID)
public final class BarroNeoForge {
    public BarroNeoForge() {
        // Run our common setup.
        Barro.init();
    }
}
