package de.timmi6790.mpstats.api.versions.v1.common.player.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Player {
    private String playerName;
}
