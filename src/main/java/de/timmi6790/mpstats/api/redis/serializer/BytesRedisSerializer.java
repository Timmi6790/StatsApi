package de.timmi6790.mpstats.api.redis.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.RequiredArgsConstructor;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.xerial.snappy.Snappy;

@RequiredArgsConstructor
public class BytesRedisSerializer<T> implements RedisSerializer<T> {
    private final JavaType javaType;
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory())
            .registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public T deserialize(@Nullable final byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        } else {
            try {
                final byte[] uncompressedBytes = Snappy.uncompress(bytes);
                return this.objectMapper.readValue(uncompressedBytes, 0, uncompressedBytes.length, this.javaType);
            } catch (final Exception ex) {
                throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public byte[] serialize(@Nullable final Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        } else {
            try {
                final byte[] bytes = this.objectMapper.writeValueAsBytes(value);
                return Snappy.compress(bytes);
            } catch (final Exception ex) {
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }
    }
}
