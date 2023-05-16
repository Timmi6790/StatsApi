package de.timmi6790.mpstats.api.utilities;

import com.google.common.collect.Lists;
import info.debatty.java.stringsimilarity.experimental.Sift4;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class SimilarityUtilities {
    /**
     * Returns values that are similar to the given source. The levenshtein distance strategy is used to compare all
     * values.
     *
     * @param <T>         type parameter
     * @param source      source to match against values
     * @param values      values to check
     * @param toString    value to string function
     * @param maxDistance max distance
     * @param limit       limit of returned elements
     * @return similar values
     */
    public <T> List<T> getSimilarityList(@NonNull final String source,
                                         @NonNull final Collection<T> values,
                                         @NonNull final Function<T, String> toString,
                                         final double maxDistance,
                                         final int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }

        final List<ComparedValue<T>> comparedValues = Lists.newArrayListWithExpectedSize(values.size());

        final Sift4 strategy = new Sift4();
        final String sourceLower = source.toLowerCase();
        for (final T value : values) {
            final double similarityValue = strategy.distance(sourceLower, toString.apply(value).toLowerCase());
            if (similarityValue <= maxDistance) {
                comparedValues.add(new ComparedValue<>(similarityValue, value));
            }
        }

        // Sort after highest similarity 
        comparedValues.sort(Comparator.comparing(ComparedValue::getSimilarity));

        final int maxValues = Math.min(limit, comparedValues.size());
        final List<T> foundValues = Lists.newArrayListWithCapacity(maxValues);
        for (int count = 0; maxValues > count; count++) {
            foundValues.add(comparedValues.get(count).getValue());
        }

        return foundValues;
    }

    @Data
    private static class ComparedValue<D> {
        private final double similarity;
        private final D value;
    }
}
