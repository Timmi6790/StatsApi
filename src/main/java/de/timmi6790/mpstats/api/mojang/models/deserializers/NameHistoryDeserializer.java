package de.timmi6790.mpstats.api.mojang.models.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import de.timmi6790.mpstats.api.mojang.models.NameHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NameHistoryDeserializer implements JsonDeserializer<NameHistory> {
    @Override
    public NameHistory deserialize(final JsonElement jsonElement,
                                   final Type type,
                                   final JsonDeserializationContext jsonDeserializationContext) {
        final List<NameHistory.NameHistoryData> nameHistory = new ArrayList<>();
        for (final JsonElement object : jsonElement.getAsJsonArray()) {
            long playerNameChangeDateTime = -1;
            if (object.getAsJsonObject().has("changedToAt")) {
                playerNameChangeDateTime = object.getAsJsonObject().get("changedToAt").getAsLong();
            }

            nameHistory.add(
                    new NameHistory.NameHistoryData(
                            object.getAsJsonObject().get("name").getAsString(),
                            playerNameChangeDateTime
                    )
            );
        }

        return new NameHistory(
                nameHistory
        );
    }
}
