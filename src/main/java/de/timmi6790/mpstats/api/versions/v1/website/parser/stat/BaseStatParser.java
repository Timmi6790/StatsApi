package de.timmi6790.mpstats.api.versions.v1.website.parser.stat;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter(AccessLevel.PROTECTED)
public abstract class BaseStatParser implements StatParser {
    private static final Pattern ILLEGAL_STAT_PATTERN = Pattern.compile("^.+(?:Kills|Deaths)$");

    private final GameService gameService;
    private final StatService statService;

    private final Map<String, String> statNameConversion = new HashMap<>();
    private final Set<String> whitelistedStats;
    private final Set<String> ignoredStats;

    private final Map<String, String> gameNameConversion = new HashMap<>();

    protected BaseStatParser(final GameService gameService, final StatService statService) {
        this.gameService = gameService;
        this.statService = statService;

        this.whitelistedStats = Set.of(
                "FinalKills",
                "SwiftKill"
        );
        this.ignoredStats = Set.of(
                "DamageDealt",
                "DamageTaken",
                "DamageTakeninPvP",
                "SWATKills",
                "SWATDeaths",
                "BombersKills",
                "BombersDeaths",
                "KDR"
        );

        this.statNameConversion.put("Cake Bites", "BigAppetite");
        this.statNameConversion.put("Perfect Build", "Dependable");
        this.statNameConversion.put("TNT Pickups", "TNTHoarder");
        this.statNameConversion.put("Supply Drops Opened", "LootHoarder");
        this.statNameConversion.put("Time Played", "TimePlaying");
        this.statNameConversion.put("Total games", "GamesPlayed");
        this.statNameConversion.put("Swift Kills", "SwiftKill");

        this.gameNameConversion.put("Bridges", "TheBridges");
        this.gameNameConversion.put("UHC", "UltraHardCore");
        this.gameNameConversion.put("Super Smash Mobs Solo", "SuperSmashMobs");
        this.gameNameConversion.put("Survival Games Solo", "SurvivalGames");
        this.gameNameConversion.put("Skywars Solo", "Skywars");
        this.gameNameConversion.put("Gladiators Solo", "Gladiators");
    }

    protected String convertStatName(final String statName) {
        return this.statNameConversion.getOrDefault(statName, statName.replace(" ", ""));
    }

    protected String convertGameName(final String gameName) {
        return this.gameNameConversion.getOrDefault(gameName, gameName.replace(" ", ""));
    }

    protected boolean isStatIgnored(final String statName) {
        if (this.whitelistedStats.contains(statName)) {
            return false;
        }

        final Matcher matcher = ILLEGAL_STAT_PATTERN.matcher(statName);
        if (matcher.find()) {
            return true;
        }

        return this.ignoredStats.contains(statName);
    }

    protected void addStat(final ParserResult parserResult,
                           final Game game,
                           final String cleanStatName,
                           final String valueText) {
        final Optional<Stat> statOpt = this.getStat(cleanStatName);
        if (statOpt.isPresent()) {
            parserResult.add(game, statOpt.get(), this.parseValue(valueText));
        } else if (!this.isStatIgnored(cleanStatName)) {
            parserResult.add(game, cleanStatName, this.parseValue(valueText));
        }
    }

    protected long parseValue(final String value) {
        return Long.parseLong(value.replace(",", ""));
    }

    protected Optional<Game> getGame(final String gameName) {
        return this.gameService.getGame(this.convertGameName(gameName));
    }

    protected Optional<Stat> getStat(final String statName) {
        return this.statService.getStat(statName);
    }
}
