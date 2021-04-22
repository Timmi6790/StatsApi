package de.timmi6790.mpstats.api.mojang.models;

import de.timmi6790.mpstats.api.mojang.MojangApi;
import lombok.NonNull;

import java.util.UUID;

public record MojangPlayer(@NonNull String name, @NonNull UUID uuid) {
    private static final String PLAYER_HEARD_URL = "https://minotar.net/avatar/%s";

    public NameHistory getNameHistory() {
        return MojangApi.getPlayerNames(this.uuid).orElseThrow(RuntimeException::new);
    }

    public String getHeadUrl() {
        return String.format(
                PLAYER_HEARD_URL,
                this.uuid.toString().replace("-", "")
        );
    }
}
