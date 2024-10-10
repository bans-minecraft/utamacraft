package net.banutama.utamacraft.util;

import com.mojang.authlib.GameProfile;
import net.banutama.utamacraft.Utamacraft;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FakeGameProfile extends GameProfile {
    private static final UUID GAME_PROFILE_UUID = UUID.nameUUIDFromBytes("Utamacraft".getBytes(StandardCharsets.UTF_8));
    private static final String GAME_PROFILE_NAME = String.format("[%s]", Utamacraft.MOD_ID);

    public FakeGameProfile() {
        super(GAME_PROFILE_UUID, GAME_PROFILE_NAME);
    }
}
