package utility;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe store for test failures.
 */
public final class FailureStore {
    private static final ConcurrentMap<String, Throwable> STORE = new ConcurrentHashMap<>();

    public static void put(String key, Throwable t) {
        if (key == null || t == null) return;
        STORE.put(key, t);
    }

    public static Throwable getAndRemove(String key) {
        if (key == null) return null;
        return STORE.remove(key);
    }

    public static boolean containsKey(String key) {
        return key != null && STORE.containsKey(key);
    }

    /**
     * Debug helper: return a snapshot of current keys in the store.
     */
    public static Set<String> currentKeys() {
        return STORE.keySet();
    }
}
