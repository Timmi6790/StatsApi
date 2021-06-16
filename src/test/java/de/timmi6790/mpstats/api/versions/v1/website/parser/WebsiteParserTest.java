package de.timmi6790.mpstats.api.versions.v1.website.parser;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import de.timmi6790.mpstats.api.versions.v1.website.models.GameStat;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayer;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebsiteParserTest {
    // Games
    private static final String GLOBAL = "Global";
    private static final String BLOCK_HUNT = "BlockHunt";
    private static final String BRIDGES = "TheBridges";
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
    private static final String ULTRA_HARDCORE = "UltraHardCore";

    // Stats
    private static final String EXP_EARNED = "EXPEarned";
    private static final String GAMES_PLAYED = "GamesPlayed";
    private static final String WINS = "Wins";
    private static final String LOSSES = "Losses";
    private static final String KILLS = "Kills";
    private static final String ASSISTS = "Assists";
    private static final String DEATHS = "Deaths";
    private static final String GEMS_EARNED = "GemsEarned";
    private static final String CHESTS_OPENED = "ChestsOpened";
    private static final String BLOCKS_PLACED = "BlocksPlaced";
    private static final String BLOCKS_BROKEN = "BlocksBroken";

    // Global Stats
    private static final String FRIENDS = "Friends";

    // CakeWars Stats
    private static final String FINAL_KILLS = "FinalKills";
    private static final String CAKE_BITES = "BigAppetite";

    // Clans Stats
    private static final String GOLD_EARNED = "GoldEarned";
    private static final String TIME_PLAYED = "TimePlaying";

    // Gladiator Stats
    private static final String SWIFT_KILLS = "SwiftKill";

    // Skywars Stats
    private static final String TNT_PICKUP = "TNTHoarder";

    // SpeedBuilder Stats
    private static final String PERFECT_BUILD = "Dependable";

    // SupplyDropsOpened Stats
    private static final String SUPPLY_DROPS_OPENED = "LootHoarder";

    @SneakyThrows
    private static String getContentFromFile(@NonNull final String path) {
        final ClassLoader classLoader = WebsiteParserTest.class.getClassLoader();

        final URI uri = classLoader.getResource(path).toURI();
        final byte[] encoded = Files.readAllBytes(Paths.get(uri));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private Optional<Game> constructGameObject(final String gameName) {
        return Optional.of(
                new Game(
                        0,
                        gameName,
                        gameName,
                        gameName,
                        new HashSet<>(),
                        gameName,
                        gameName,
                        gameName
                )
        );
    }

    private WebsiteParser setUpWebsiteStats(final HttpUrl url) {
        final JavaGameService gameService = mock(JavaGameService.class);
        return this.setUpWebsiteStats(url, gameService);
    }

    private WebsiteParser setUpWebsiteStats(final HttpUrl url, final JavaGameService gameService) {
        final JavaStatService statService = mock(JavaStatService.class);

        final WebsiteParser websiteStats = spy(new WebsiteParser(gameService, statService));
        doReturn(new Request.Builder().url(url).build())
                .when(websiteStats)
                .constructRequest(any());

        return websiteStats;
    }

    private void checkIfContains(final Map<String, Long> statModels, final String stat, final long value) {
        assertThat(statModels).containsEntry(stat, value);
    }

    private void checkIfContains(final GameStat gameStat, final Map<String, Long> values) {
        for (final Map.Entry<String, Long> value : values.entrySet()) {
            this.checkIfContains(gameStat.getOtherStats(), value.getKey(), value.getValue());
        }
    }

    @Test
    void retrievePlayerStats_Empty() {
        try (final MockWebServer server = new MockWebServer()) {
            final String playerName = "Empty";
            server.enqueue(new MockResponse().setBody(getContentFromFile("website/" + playerName)));

            final HttpUrl url = server.url("");
            final WebsiteParser websiteParser = this.setUpWebsiteStats(url);

            final Optional<WebsitePlayer> dataOpt = websiteParser.retrievePlayerStats(playerName);
            assertThat(dataOpt).isEmpty();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void retrievePlayerStats_Timmi() {
        final String playerName = "Timmi6790";

        final JavaGameService gameService = mock(JavaGameService.class);
        when(gameService.getGame(any())).then((Answer<Optional<Game>>) invocation -> {
            final String gameName = invocation.getArgument(0, String.class);
            return this.constructGameObject(gameName);
        });

        final WebsitePlayer data;
        try (final MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody(getContentFromFile("website/" + playerName)));

            final HttpUrl url = server.url("");
            final WebsiteParser websiteParser = this.setUpWebsiteStats(url, gameService);

            final Optional<WebsitePlayer> dataOpt = websiteParser.retrievePlayerStats(playerName);
            assertThat(dataOpt).isPresent();

            data = dataOpt.get();
        } catch (final IOException e) {
            e.printStackTrace();
            fail("IoException");
            return;
        }

        // General data
        assertThat(data.getPlayerName()).isEqualTo(playerName);
        assertThat(data.getPrimaryRank()).isEqualTo("Eternal");
        assertThat(data.getPlayerUUID()).isEqualTo(UUID.fromString("9d59daad-6f62-4bd9-b13e-c961bf906750"));

        // Stats
        final Map<Game, GameStat> stats = data.getGameStats();

        // Global
        this.checkIfContains(
                stats.get(gameService.getGame(GLOBAL).orElseThrow()),
                Map.of(
                        FRIENDS, 83L,
                        EXP_EARNED, 6184777L,
                        GAMES_PLAYED, 10455L
                )
        );

        // BlockHunt
        this.checkIfContains(
                stats.get(gameService.getGame(BLOCK_HUNT).orElseThrow()),
                Map.of(
                        WINS, 14L,
                        LOSSES, 40L,
                        KILLS, 99L,
                        ASSISTS, 13L,
                        DEATHS, 67L,
                        GEMS_EARNED, 8871L,
                        EXP_EARNED, 20172L
                )
        );

        // Bridges
        this.checkIfContains(
                stats.get(gameService.getGame(BRIDGES).orElseThrow()),
                Map.of(
                        WINS, 9L,
                        LOSSES, 18L,
                        KILLS, 74L,
                        ASSISTS, 11L,
                        DEATHS, 33L,
                        GEMS_EARNED, 14785L,
                        EXP_EARNED, 19322L
                )
        );

        // CakeWarsDuos
        this.checkIfContains(
                stats.get(gameService.getGame(CAKE_WARS_DUOS).orElseThrow()),
                Map.of(
                        WINS, 72L,
                        LOSSES, 24L,
                        KILLS, 794L,
                        ASSISTS, 188L,
                        DEATHS, 345L,
                        FINAL_KILLS, 279L,
                        CAKE_BITES, 1306L,
                        CHESTS_OPENED, 34L,
                        GEMS_EARNED, 31176L,
                        EXP_EARNED, 104844L
                )
        );

        // CakeWarsStandard
        this.checkIfContains(
                stats.get(gameService.getGame(CAKE_WARS_STANDARD).orElseThrow()),
                Map.of(
                        WINS, 86L,
                        LOSSES, 76L,
                        KILLS, 849L,
                        ASSISTS, 330L,
                        DEATHS, 575L,
                        FINAL_KILLS, 250L,
                        CAKE_BITES, 547L,
                        CHESTS_OPENED, 23L,
                        GEMS_EARNED, 42760L,
                        EXP_EARNED, 126666L
                )
        );

        // ChampionsCTF
        this.checkIfContains(
                stats.get(gameService.getGame(CHAMPIONS_CTF).orElseThrow()),
                Map.of(
                        WINS, 155L,
                        LOSSES, 54L,
                        KILLS, 1537L,
                        ASSISTS, 1304L,
                        DEATHS, 603L,
                        GEMS_EARNED, 28714L,
                        EXP_EARNED, 140279L
                )
        );

        // ChampionsDomination
        this.checkIfContains(
                stats.get(gameService.getGame(CHAMPIONS_DOMINATION).orElseThrow()),
                Map.of(
                        WINS, 35L,
                        LOSSES, 74L,
                        KILLS, 465L,
                        ASSISTS, 208L,
                        DEATHS, 315L,
                        GEMS_EARNED, 11924L,
                        EXP_EARNED, 26898L
                )
        );

        // ChampionsTDM
        this.checkIfContains(
                stats.get(gameService.getGame(CHAMPIONS_TDM).orElseThrow()),
                Map.of(
                        WINS, 205L,
                        LOSSES, 98L,
                        KILLS, 475L,
                        ASSISTS, 209L,
                        DEATHS, 140L,
                        GEMS_EARNED, 29924L,
                        EXP_EARNED, 33660L
                )
        );

        // Clans
        this.checkIfContains(
                stats.get(gameService.getGame(CLANS).orElseThrow()),
                Map.of(
                        DEATHS, 259L,
                        GOLD_EARNED, 352000L,
                        TIME_PLAYED, TimeUnit.DAYS.toSeconds(25)
                )
        );

        // DrawMyThing
        this.checkIfContains(
                stats.get(gameService.getGame(DRAW_MY_THING).orElseThrow()),
                Map.of(
                        WINS, 4L,
                        LOSSES, 16L,
                        GEMS_EARNED, 2695L,
                        EXP_EARNED, 10866L
                )
        );

        // Gladiators
        this.checkIfContains(
                stats.get(gameService.getGame(GLADIATORS).orElseThrow()),
                Map.of(
                        WINS, 115L,
                        LOSSES, 136L,
                        KILLS, 629L,
                        ASSISTS, 0L,
                        DEATHS, 138L,
                        SWIFT_KILLS, 81L,
                        GEMS_EARNED, 37430L,
                        EXP_EARNED, 80812L
                )
        );

        // MasterBuilders
        this.checkIfContains(
                stats.get(gameService.getGame(MASTER_BUILDERS).orElseThrow()),
                Map.of(
                        WINS, 0L,
                        LOSSES, 27L,
                        BLOCKS_PLACED, 4696L,
                        BLOCKS_BROKEN, 972L,
                        GEMS_EARNED, 2679L,
                        EXP_EARNED, 10103L
                )
        );

        // MineStrike
        this.checkIfContains(
                stats.get(gameService.getGame(MINE_STRIKE).orElseThrow()),
                Map.of(
                        WINS, 4L,
                        LOSSES, 1L,
                        KILLS, 81L,
                        ASSISTS, 29L,
                        DEATHS, 47L,
                        GEMS_EARNED, 1768L,
                        EXP_EARNED, 1855L
                )
        );

        // Skywars
        this.checkIfContains(
                stats.get(gameService.getGame(SKYWARS).orElseThrow()),
                Map.of(
                        WINS, 156L,
                        LOSSES, 215L,
                        KILLS, 901L,
                        ASSISTS, 46L,
                        DEATHS, 228L,
                        TNT_PICKUP, 450L,
                        GEMS_EARNED, 79715L,
                        EXP_EARNED, 185838L
                )
        );

        // SkywarsTeams
        this.checkIfContains(
                stats.get(gameService.getGame(SKYWARS_TEAMS).orElseThrow()),
                Map.of(
                        WINS, 198L,
                        LOSSES, 219L,
                        KILLS, 1267L,
                        ASSISTS, 327L,
                        DEATHS, 251L,
                        TNT_PICKUP, 526L,
                        GEMS_EARNED, 95963L,
                        EXP_EARNED, 235200L
                )
        );

        // SpeedBuilders
        this.checkIfContains(
                stats.get(gameService.getGame(SPEED_BUILDERS).orElseThrow()),
                Map.of(
                        WINS, 7L,
                        LOSSES, 12L,
                        BLOCKS_PLACED, 4185L,
                        PERFECT_BUILD, 59L,
                        GEMS_EARNED, 1903L,
                        EXP_EARNED, 8834L
                )
        );

        // SuperSmashMobs
        this.checkIfContains(
                stats.get(gameService.getGame(SUPER_SMASH_MOBS).orElseThrow()),
                Map.of(
                        WINS, 724L,
                        LOSSES, 420L,
                        KILLS, 6289L,
                        ASSISTS, 1710L,
                        DEATHS, 2814L,
                        GEMS_EARNED, 284478L,
                        EXP_EARNED, 803851L
                )
        );

        // SuperSmashMobsTeams
        this.checkIfContains(
                stats.get(gameService.getGame(SUPER_SMASH_MOBS_TEAMS).orElseThrow()),
                Map.of(
                        WINS, 1430L,
                        LOSSES, 200L,
                        KILLS, 8725L,
                        ASSISTS, 5079L,
                        DEATHS, 2909L,
                        GEMS_EARNED, 398664L,
                        EXP_EARNED, 1112246L
                )
        );

        // SurvivalGames
        this.checkIfContains(
                stats.get(gameService.getGame(SURVIVAL_GAMES).orElseThrow()),
                Map.of(
                        WINS, 97L,
                        LOSSES, 76L,
                        KILLS, 178L,
                        ASSISTS, 13L,
                        DEATHS, 81L,
                        SUPPLY_DROPS_OPENED, 11L,
                        GEMS_EARNED, 25788L,
                        EXP_EARNED, 37841L
                )
        );

        // SurvivalGamesTeams
        this.checkIfContains(
                stats.get(gameService.getGame(SURVIVAL_GAMES_TEAMS).orElseThrow()),
                Map.of(
                        WINS, 1L,
                        LOSSES, 19L,
                        KILLS, 18L,
                        ASSISTS, 7L,
                        DEATHS, 20L,
                        SUPPLY_DROPS_OPENED, 3L,
                        GEMS_EARNED, 1756L,
                        EXP_EARNED, 6011L
                )
        );

        // UHC
        this.checkIfContains(
                stats.get(gameService.getGame(ULTRA_HARDCORE).orElseThrow()),
                Map.of(
                        WINS, 0L,
                        LOSSES, 4L,
                        KILLS, 3L,
                        ASSISTS, 0L,
                        DEATHS, 4L,
                        GEMS_EARNED, 929L,
                        EXP_EARNED, 1003L
                )
        );
    }
}