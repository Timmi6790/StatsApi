package de.timmi6790.mpstats.api.versions.v1.website.parser;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WebsiteConverter {
    private static final String CLANS_GAME_NAME = "Clans";

    public long convertValue(final String game, final String stat, final String value) {
        if (game.equalsIgnoreCase(CLANS_GAME_NAME) && stat.equalsIgnoreCase("Time Played")) {
            return TimeUnit.DAYS.toSeconds(Long.parseLong(value.replace(",", "")));
        }

        return Long.parseLong(value.replace(",", ""));
    }

    public String convertGame(final String game) {
        return game
                .replace(" ", "")
                .replace("Solo", "");
    }

    public String convertStat(final String game, final String stat) {
        return stat;
    }
}
