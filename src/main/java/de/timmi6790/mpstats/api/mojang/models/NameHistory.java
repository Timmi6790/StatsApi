package de.timmi6790.mpstats.api.mojang.models;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


public record NameHistory(List<NameHistoryData> history) {
    public record NameHistoryData(String name, long changedAt) {
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
