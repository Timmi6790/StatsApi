package de.timmi6790.mpstats.api.mojang.models.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import de.timmi6790.mpstats.api.mojang.models.MojangPlayer;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.regex.Pattern;

public class MojangUserDeserializer implements JsonDeserializer<MojangPlayer> {
    private static final Pattern FULL_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    @Override
    public MojangPlayer deserialize(final JsonElement jsonElement,
                                    final Type type,
                                    final JsonDeserializationContext jsonDeserializationContext) {
        final String uuidShort = jsonElement.getAsJsonObject().get("id").getAsString();
        final String uuid = FULL_UUID_PATTERN.matcher(uuidShort).replaceAll("$1-$2-$3-$4-$5");
        return new MojangPlayer(
                jsonElement.getAsJsonObject().get("name").getAsString(),
                UUID.fromString(uuid)
        );
    }
}
