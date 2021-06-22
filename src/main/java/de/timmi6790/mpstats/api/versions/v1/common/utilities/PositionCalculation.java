package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class PositionCalculation {
    private int globalPosition;
    private int position = this.globalPosition;
    private long lastScore = Long.MIN_VALUE;

    public PositionCalculation() {
        this(1);
    }

    public PositionCalculation(final int startPosition) {
        this.globalPosition = startPosition - 1;
    }

    public int addScore(final long score) {
        this.globalPosition++;
        if (score != this.lastScore) {
            this.position = this.globalPosition;
        }
        this.lastScore = score;

        return this.position;
    }
}
