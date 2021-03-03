package de.timmi6790.mpstats.api.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mpstats.api.apikey.ApiKey;
import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    private final ApiKeyService apiKeyService;

    @Autowired
    public RateLimitService(final ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    private Bucket createBucket(final int minuteLimit, final int dayLimit) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(minuteLimit, Refill.intervally(minuteLimit, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(dayLimit, Refill.intervally(dayLimit, Duration.ofDays(1))))
                .build();
    }

    private Bucket newDefaultBucket() {
        return this.createBucket(30, 1_000);
    }

    private Optional<ApiKey> getApiKey(@Nullable final String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Optional.empty();
        }

        return this.apiKeyService.getApiKey(apiKey);
    }

    /**
     * Gets the current cooldown bucket for the input. If the api key exists and is valid it will return a bucket based
     * on the apiKey. Otherwise, the bucket is based on the ip.
     *
     * @param apiKey    the api key
     * @param ipAddress the ip address
     * @return the bucket
     */
    public Bucket resolveBucket(final @Nullable String apiKey, final String ipAddress) {
        // check if the api is valid
        final Optional<ApiKey> apiKeyOpt = this.getApiKey(apiKey);
        if (apiKeyOpt.isPresent()) {
            final ApiKey key = apiKeyOpt.get();
            return this.cache.get(
                    apiKey,
                    k -> this.createBucket(key.getMinuteRateLimitLimit(), key.getDailyRateLimit())
            );
        }

        return this.cache.get(ipAddress, key -> this.newDefaultBucket());
    }
}
