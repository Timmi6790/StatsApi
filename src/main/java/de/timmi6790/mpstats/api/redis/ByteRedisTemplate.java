package de.timmi6790.mpstats.api.redis;

import com.fasterxml.jackson.databind.JavaType;
import de.timmi6790.mpstats.api.redis.serializer.BytesRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class ByteRedisTemplate<V> extends RedisTemplate<String, V> {
    public ByteRedisTemplate(final RedisConnectionFactory connectionFactory, final JavaType javaType) {
        final BytesRedisSerializer<V> snappyMsgPackSerializer = new BytesRedisSerializer<>(javaType);

        this.setConnectionFactory(connectionFactory);
        this.setDefaultSerializer(snappyMsgPackSerializer);

        // Keys
        this.setKeySerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());

        // Values
        this.setValueSerializer(snappyMsgPackSerializer);
        this.setHashValueSerializer(snappyMsgPackSerializer);

        this.afterPropertiesSet();
    }
}
