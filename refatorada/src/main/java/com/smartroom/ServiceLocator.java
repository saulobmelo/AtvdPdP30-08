
package main.java.com.smartroom;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple Service Locator for dependency resolution.
 * Avoids new-ing dependencies across the codebase.
 * You can swap implementations by changing registrations here.
 */
public class ServiceLocator {
    private static final Map<Class<?>, Object> services = new HashMap<>();
    private static final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

    public static <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    public static <T> void registerFactory(Class<T> type, Supplier<T> supplier) {
        factories.put(type, supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> T resolve(Class<T> type) {
        Object svc = services.get(type);
        if (svc != null) return (T) svc;
        Supplier<?> sup = factories.get(type);
        if (sup != null) {
            T inst = (T) sup.get();
            services.put(type, inst);
            return inst;
        }
        throw new IllegalStateException("No service registered for: " + type.getName());
    }

    public static void clear() {
        services.clear();
        factories.clear();
    }
}
