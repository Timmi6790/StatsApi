package de.timmi6790.mpstats.api.mojang.models;

import de.timmi6790.mpstats.api.mojang.MojangApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MojangPlayer {
    private static final String PLAYER_HEARD_URL = "https://minotar.net/avatar/%s";

    @NonNull
    private final String name;
    @NonNull
    private final UUID uuid;

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
