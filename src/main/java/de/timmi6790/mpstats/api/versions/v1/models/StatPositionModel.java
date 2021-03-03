package de.timmi6790.mpstats.api.versions.v1.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class StatPositionModel extends StatModel {
    private final int position;

    public StatPositionModel(final String stat, final long score, final int position) {
        super(stat, score);
        this.position = position;
    }
}
