package de.timmi6790.mpstats.api.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
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
        return this.apiKeyService.getApiKey(apiKey);
    }

    public void invalidateCache() {
        this.cache.invalidateAll();
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
        return this.getApiKey(apiKey).map(key -> {
            final RateLimit rateLimit = key.getRateLimit();
            return this.cache.get(
                    apiKey,
                    k -> this.createBucket(rateLimit.getMinute(), rateLimit.getDaily())
            );
        }).orElseGet(() -> this.cache.get(ipAddress, key -> this.newDefaultBucket()));
    }
}
