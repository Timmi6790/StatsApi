package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BedrockPlayer implements Player {
    private String playerName;

    @Override
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
}
