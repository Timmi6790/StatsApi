package de.timmi6790.mpstats.api.utilities;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class DataUtilities {
    /**
     * Converts the given collection into a different type collection
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @param toType     to conversion function
     * @return the converted string list
     */
    public <T, R> List<R> convertToTypeList(final Collection<T> collection, final Function<T, R> toType) {
        final List<R> result = Lists.newArrayListWithCapacity(collection.size());
        for (final T value : collection) {
            result.add(toType.apply(value));
        }
        return result;
    }
}
