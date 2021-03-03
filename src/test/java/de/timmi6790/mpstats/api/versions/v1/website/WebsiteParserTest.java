package de.timmi6790.mpstats.api.versions.v1.website;

import de.timmi6790.commons.builders.MapBuilder;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayerModel;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class WebsiteParserTest {
    // Games
    private static final String GLOBAL = "Global";
    private static final String BLOCK_HUNT = "BlockHunt";
    private static final String BRIDGES = "Bridges";
    private static final String CAKE_WARS_DUOS = "CakeWarsDuos";
    private static final String CAKE_WARS_STANDARD = "CakeWarsStandard";
    private static final String CHAMPIONS_CTF = "ChampionsCTF";
    private static final String CHAMPIONS_DOMINATION = "ChampionsDomination";
    private static final String CHAMPIONS_TDM = "ChampionsTDM";
    private static final String CLANS = "Clans";
    private static final String DRAW_MY_THING = "DrawMyThing";
    private static final String GLADIATORS = "Gladiators";
    private static final String MASTER_BUILDERS = "MasterBuilders";
    private static final String MINE_STRIKE = "MineStrike";
    private static final String SKYWARS = "Skywars";
    private static final String SKYWARS_TEAMS = "SkywarsTeams";
    private static final String SPEED_BUILDERS = "SpeedBuilders";
    private static final String SUPER_SMASH_MOBS = "SuperSmashMobs";
    private static final String SUPER_SMASH_MOBS_TEAMS = "SuperSmashMobsTeams";
    private static final String SURVIVAL_GAMES = "SurvivalGames";
    private static final String SURVIVAL_GAMES_TEAMS = "SurvivalGamesTeams";
    private static final String ULTRA_HARDCORE = "UHC";

    // Stats
    private static final String EXP_EARNED = "EXP Earned";
    private static final String GAMES_PLAYED = "Games Played";
    private static final String WINS = "Wins";
    private static final String LOSSES = "Losses";
    private static final String KILLS = "Kills";
    private static final String ASSISTS = "Assists";
    private static final String DEATHS = "Deaths";
    private static final String GEMS_EARNED = "Gems Earned";
    private static final String CHESTS_OPENED = "Chests Opened";
    private static final String BLOCKS_PLACED = "Blocks Placed";
    private static final String BLOCKS_BROKEN = "Blocks Broken";

    // Global Stats
    private static final String FRIENDS = "Friends";

    // BlockHunt Stats
    private static final String HUNTER_KILLS = "Hunter Kills";
    private static final String HIDER_KILLS = "Hiders Kills";
    private static final String HUNTER_DEATHS = "Hunter Deaths";
    private static final String HIDER_DEATHS = "Hiders Deaths";

    // CakeWars Stats
    private static final String FINAL_KILLS = "Final Kills";
    private static final String CAKE_BITES = "Cake Bites";

    // Clans Stats
    private static final String GOLD_EARNED = "Gold Earned";
    private static final String TIME_PLAYED = "Time Played";

    // Gladiator Stats
    private static final String SWIFT_KILLS = "Swift Kills";

    // Skywars Stats
    private static final String TNT_PICKUP = "TNT Pickups";

    // SpeedBuilder Stats
    private static final String PERFECT_BUILD = "Perfect Build";

    // SupplyDropsOpened Stats
    private static final String SUPPLY_DROPS_OPENED = "Supply Drops Opened";

    @SneakyThrows
    private static String getContentFromFile(@NonNull final String path) {
        final ClassLoader classLoader = WebsiteParserTest.class.getClassLoader();

        final URI uri = classLoader.getResource(path).toURI();
        final byte[] encoded = Files.readAllBytes(Paths.get(uri));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private WebsiteParser setUpWebsiteStats(final String htmlContentPath) {
        final WebsiteParser websiteStats = spy(new WebsiteParser());
        final String htmlContent = getContentFromFile(htmlContentPath);
        doReturn(Optional.of(htmlContent))
                .when(websiteStats)
                .getHtmlString(any());

        return websiteStats;
    }

    private void checkIfContains(final Map<String, Long> statModels, final String stat, final long value) {
        assertThat(statModels).containsEntry(stat, value);
    }

    private void checkIfContains(final Map<String, Long> statModels, final Map<String, Long> values) {
        for (final Map.Entry<String, Long> value : values.entrySet()) {
            this.checkIfContains(statModels, value.getKey(), value.getValue());
        }
    }

    @Test
    void retrievePlayerStats_Empty() {
        final String playerName = "Empty";

        final WebsiteParser websiteParser = this.setUpWebsiteStats("website/" + playerName);

        final Optional<WebsitePlayerModel> dataOpt = websiteParser.retrievePlayerStats(playerName);
        assertThat(dataOpt).isEmpty();
    }

    @Test
    void retrievePlayerStats_Timmi() {
        final String playerName = "Timmi6790";

        final WebsiteParser websiteParser = this.setUpWebsiteStats("website/" + playerName);

        final Optional<WebsitePlayerModel> dataOpt = websiteParser.retrievePlayerStats(playerName);
        assertThat(dataOpt).isPresent();

        final WebsitePlayerModel data = dataOpt.get();

        // General data
        assertThat(data.getPlayerName()).isEqualTo(playerName);
        assertThat(data.getPrimaryRank()).isEqualTo("Eternal");
        assertThat(data.getPlayerUUID()).isEqualTo(UUID.fromString("9d59daad-6f62-4bd9-b13e-c961bf906750"));

        // Stats
        final Map<String, Map<String, Long>> stats = data.getStats();

        // Global
        this.checkIfContains(
                stats.get(GLOBAL),
                MapBuilder.<String, Long>ofHashMap()
                        .put(FRIENDS, 83L)
                        .put(EXP_EARNED, 6184777L)
                        .put(GAMES_PLAYED, 10455L)
                        .build()
        );

        // BlockHunt
        this.checkIfContains(
                stats.get(BLOCK_HUNT),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 14L)
                        .put(LOSSES, 40L)
                        .put(KILLS, 99L)
                        .put(ASSISTS, 13L)
                        .put(DEATHS, 67L)
                        .put(HUNTER_KILLS, 4L)
                        .put(HIDER_KILLS, 1L)
                        .put(HUNTER_DEATHS, 0L)
                        .put(HIDER_DEATHS, 4L)
                        .put(GEMS_EARNED, 8871L)
                        .put(EXP_EARNED, 20172L)
                        .build()
        );

        // Bridges
        this.checkIfContains(
                stats.get(BRIDGES),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 9L)
                        .put(LOSSES, 18L)
                        .put(KILLS, 74L)
                        .put(ASSISTS, 11L)
                        .put(DEATHS, 33L)
                        .put(GEMS_EARNED, 14785L)
                        .put(EXP_EARNED, 19322L)
                        .build()
        );

        // CakeWarsDuos
        this.checkIfContains(
                stats.get(CAKE_WARS_DUOS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 72L)
                        .put(LOSSES, 24L)
                        .put(KILLS, 794L)
                        .put(ASSISTS, 188L)
                        .put(DEATHS, 345L)
                        .put(FINAL_KILLS, 279L)
                        .put(CAKE_BITES, 1306L)
                        .put(CHESTS_OPENED, 34L)
                        .put(GEMS_EARNED, 31176L)
                        .put(EXP_EARNED, 104844L)
                        .build()
        );

        // CakeWarsStandard
        this.checkIfContains(
                stats.get(CAKE_WARS_STANDARD),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 86L)
                        .put(LOSSES, 76L)
                        .put(KILLS, 849L)
                        .put(ASSISTS, 330L)
                        .put(DEATHS, 575L)
                        .put(FINAL_KILLS, 250L)
                        .put(CAKE_BITES, 547L)
                        .put(CHESTS_OPENED, 23L)
                        .put(GEMS_EARNED, 42760L)
                        .put(EXP_EARNED, 126666L)
                        .build()
        );

        // ChampionsCTF
        this.checkIfContains(
                stats.get(CHAMPIONS_CTF),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 155L)
                        .put(LOSSES, 54L)
                        .put(KILLS, 1537L)
                        .put(ASSISTS, 1304L)
                        .put(DEATHS, 603L)
                        .put(GEMS_EARNED, 28714L)
                        .put(EXP_EARNED, 140279L)
                        .build()
        );

        // ChampionsDomination
        this.checkIfContains(
                stats.get(CHAMPIONS_DOMINATION),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 35L)
                        .put(LOSSES, 74L)
                        .put(KILLS, 465L)
                        .put(ASSISTS, 208L)
                        .put(DEATHS, 315L)
                        .put(GEMS_EARNED, 11924L)
                        .put(EXP_EARNED, 26898L)
                        .build()
        );

        // ChampionsTDM
        this.checkIfContains(
                stats.get(CHAMPIONS_TDM),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 205L)
                        .put(LOSSES, 98L)
                        .put(KILLS, 475L)
                        .put(ASSISTS, 209L)
                        .put(DEATHS, 140L)
                        .put(GEMS_EARNED, 29924L)
                        .put(EXP_EARNED, 33660L)
                        .build()
        );

        // Clans
        this.checkIfContains(
                stats.get(CLANS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(DEATHS, 259L)
                        .put(GOLD_EARNED, 352000L)
                        .put(TIME_PLAYED, TimeUnit.DAYS.toSeconds(25))
                        .build()
        );

        // DrawMyThing
        this.checkIfContains(
                stats.get(DRAW_MY_THING),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 4L)
                        .put(LOSSES, 16L)
                        .put(GEMS_EARNED, 2695L)
                        .put(EXP_EARNED, 10866L)
                        .build()
        );

        // Gladiators
        this.checkIfContains(
                stats.get(GLADIATORS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 115L)
                        .put(LOSSES, 136L)
                        .put(KILLS, 629L)
                        .put(ASSISTS, 0L)
                        .put(DEATHS, 138L)
                        .put(SWIFT_KILLS, 81L)
                        .put(GEMS_EARNED, 37430L)
                        .put(EXP_EARNED, 80812L)
                        .build()
        );

        // MasterBuilders
        this.checkIfContains(
                stats.get(MASTER_BUILDERS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 0L)
                        .put(LOSSES, 27L)
                        .put(BLOCKS_PLACED, 4696L)
                        .put(BLOCKS_BROKEN, 972L)
                        .put(GEMS_EARNED, 2679L)
                        .put(EXP_EARNED, 10103L)
                        .build()
        );

        // MineStrike
        this.checkIfContains(
                stats.get(MINE_STRIKE),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 4L)
                        .put(LOSSES, 1L)
                        .put(KILLS, 81L)
                        .put(ASSISTS, 29L)
                        .put(DEATHS, 47L)
                        .put(GEMS_EARNED, 1768L)
                        .put(EXP_EARNED, 1855L)
                        .build()
        );

        // Skywars
        this.checkIfContains(
                stats.get(SKYWARS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 156L)
                        .put(LOSSES, 215L)
                        .put(KILLS, 901L)
                        .put(ASSISTS, 46L)
                        .put(DEATHS, 228L)
                        .put(TNT_PICKUP, 450L)
                        .put(GEMS_EARNED, 79715L)
                        .put(EXP_EARNED, 185838L)
                        .build()
        );

        // SkywarsTeams
        this.checkIfContains(
                stats.get(SKYWARS_TEAMS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 198L)
                        .put(LOSSES, 219L)
                        .put(KILLS, 1267L)
                        .put(ASSISTS, 327L)
                        .put(DEATHS, 251L)
                        .put(TNT_PICKUP, 526L)
                        .put(GEMS_EARNED, 95963L)
                        .put(EXP_EARNED, 235200L)
                        .build()
        );

        // SpeedBuilders
        this.checkIfContains(
                stats.get(SPEED_BUILDERS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 7L)
                        .put(LOSSES, 12L)
                        .put(BLOCKS_PLACED, 4185L)
                        .put(PERFECT_BUILD, 59L)
                        .put(GEMS_EARNED, 1903L)
                        .put(EXP_EARNED, 8834L)
                        .build()
        );

        // SuperSmashMobs
        this.checkIfContains(
                stats.get(SUPER_SMASH_MOBS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 724L)
                        .put(LOSSES, 420L)
                        .put(KILLS, 6289L)
                        .put(ASSISTS, 1710L)
                        .put(DEATHS, 2814L)
                        .put(GEMS_EARNED, 284478L)
                        .put(EXP_EARNED, 803851L)
                        .build()
        );

        // SuperSmashMobsTeams
        this.checkIfContains(
                stats.get(SUPER_SMASH_MOBS_TEAMS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 1430L)
                        .put(LOSSES, 200L)
                        .put(KILLS, 8725L)
                        .put(ASSISTS, 5079L)
                        .put(DEATHS, 2909L)
                        .put(GEMS_EARNED, 398664L)
                        .put(EXP_EARNED, 1112246L)
                        .build()
        );

        // SurvivalGames
        this.checkIfContains(
                stats.get(SURVIVAL_GAMES),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 97L)
                        .put(LOSSES, 76L)
                        .put(KILLS, 178L)
                        .put(ASSISTS, 13L)
                        .put(DEATHS, 81L)
                        .put(SUPPLY_DROPS_OPENED, 11L)
                        .put(GEMS_EARNED, 25788L)
                        .put(EXP_EARNED, 37841L)
                        .build()
        );

        // SurvivalGamesTeams
        this.checkIfContains(
                stats.get(SURVIVAL_GAMES_TEAMS),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 1L)
                        .put(LOSSES, 19L)
                        .put(KILLS, 18L)
                        .put(ASSISTS, 7L)
                        .put(DEATHS, 20L)
                        .put(SUPPLY_DROPS_OPENED, 3L)
                        .put(GEMS_EARNED, 1756L)
                        .put(EXP_EARNED, 6011L)
                        .build()
        );

        // UHC
        this.checkIfContains(
                stats.get(ULTRA_HARDCORE),
                MapBuilder.<String, Long>ofHashMap()
                        .put(WINS, 0L)
                        .put(LOSSES, 4L)
                        .put(KILLS, 3L)
                        .put(ASSISTS, 0L)
                        .put(DEATHS, 4L)
                        .put(GEMS_EARNED, 929L)
                        .put(EXP_EARNED, 1003L)
                        .build()
        );
    }
}