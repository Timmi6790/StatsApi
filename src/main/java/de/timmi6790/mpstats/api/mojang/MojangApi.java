package de.timmi6790.mpstats.api.mojang;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.timmi6790.mpstats.api.mojang.models.MojangPlayer;
import de.timmi6790.mpstats.api.mojang.models.NameHistory;
import de.timmi6790.mpstats.api.mojang.models.deserializers.MojangUserDeserializer;
import de.timmi6790.mpstats.api.mojang.models.deserializers.NameHistoryDeserializer;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class MojangApi {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(MojangPlayer.class, new MojangUserDeserializer())
            .registerTypeAdapter(NameHistory.class, new NameHistoryDeserializer())
            .create();

    private final LoadingCache<String, Optional<MojangPlayer>> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(playerName ->
                    parseJsonResponse(
                            Unirest.get("https://api.mojang.com/users/profiles/minecraft/{player}")
                                    .routeParam("player", playerName),
                            MojangPlayer.class
                    )
            );

    private <T> Optional<T> parseJsonResponse(final GetRequest getRequest, final Class<T> clazz) {
        final HttpResponse<String> response;
        try {
            response = getRequest.asString();
        } catch (final Exception e) {
            return Optional.empty();
        }

        if (!response.isSuccess() || response.getBody().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(gson.fromJson(response.getBody(), clazz));
    }

    public Optional<MojangPlayer> getPlayer(final String playerName) {
        return playerCache.get(playerName);
    }

    public Optional<NameHistory> getPlayerNames(final UUID uuid) {
        return parseJsonResponse(
                Unirest.get("https://api.mojang.com/user/profiles/{uuid}/names")
                        .routeParam("uuid", uuid.toString().replace("-", "")),
                NameHistory.class
        );
    }
}
