package de.unistuttgart.iste.gits.content_service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchQueryUtils {

    /**
     * Maps a list of UUIDs to a list of objects of type T. If the UUID is not found in the map, the default value is used.
     * The order of the resulting list is the same as the order of the UUIDs.
     *
     * @param uuidToObjectMap map of UUIDs to objects of type T
     * @param uuidsSorted     list of UUIDs
     * @param defaultValue    default value if UUID is not found in the map
     * @param <T>             type of the objects
     * @return list of objects of type T
     */
    public static <T> List<T> mapToSortedList(Map<UUID, T> uuidToObjectMap, List<UUID> uuidsSorted, T defaultValue) {
        return uuidsSorted.stream()
                .map(uuid -> uuidToObjectMap.getOrDefault(uuid, defaultValue))
                .toList();
    }
}
