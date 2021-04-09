package de.timmi6790.mpstats.api.mojang.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Data
@AllArgsConstructor
public class NameHistory {
    private final List<NameHistoryData> history;

    @Data
    @AllArgsConstructor
    public static class NameHistoryData {
        private final String name;
        private final long changedAt;

        public String getFormattedTime() {
            if (this.changedAt == -1) {
                return "Original";
            }

            final SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
            formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatDate.format(this.changedAt);
        }
    }
}
