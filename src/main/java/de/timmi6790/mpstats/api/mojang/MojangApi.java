package de.timmi6790.mpstats.api.mojang;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.timmi6790.mpstats.api.mojang.models.MojangPlayer;
import de.timmi6790.mpstats.api.mojang.models.NameHistory;
import de.timmi6790.mpstats.api.mojang.models.deserializers.MojangUserDeserializer;
import de.timmi6790.mpstats.api.mojang.models.deserializers.NameHistoryDeserializer;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
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
            .build(playerName -> {
                        final Request request = new Request.Builder()
                                .url("https://api.mojang.com/users/profiles/minecraft/" + playerName)
                                .build();

                        try (final Response response = new OkHttpClient().newCall(request).execute()) {
                            return parseJsonResponse(
                                    response,
                                    MojangPlayer.class
                            );
                        } catch (final IOException e) {
                            return Optional.empty();
                        }
                    }
            );

    private <T> Optional<T> parseJsonResponse(final Response response, final Class<T> clazz) {
        if (response.isSuccessful()) {
            final String body;
            try {
                body = response.body().string();
            } catch (final IOException e) {
                return Optional.empty();
            }
            return Optional.ofNullable(gson.fromJson(body, clazz));
        }
        return Optional.empty();
    }

    public Optional<MojangPlayer> getPlayer(final String playerName) {
        return playerCache.get(playerName);
    }

    public Optional<NameHistory> getPlayerNames(final UUID uuid) {
        final Request request = new Request.Builder()
                .url(
                        String.format(
                                "https://api.mojang.com/user/profiles/%s/names",
                                uuid.toString().replace("-", "")
                        )
                )
                .build();

        try (final Response response = new OkHttpClient().newCall(request).execute()) {
            return parseJsonResponse(
                    response,
                    NameHistory.class
            );
        } catch (final IOException e) {
            return Optional.empty();
        }
    }
}
