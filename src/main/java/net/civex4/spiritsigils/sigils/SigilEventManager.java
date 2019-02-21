package net.civex4.spiritsigils.sigils;

import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor;
import net.civex4.spiritsigils.SpiritSigils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SigilEventManager {
    private final Map<Sigil, Map<Class, Set<Consumer>>> listeners = new HashMap<>();
    private final Set<Class<?>> appliedListeners = new HashSet<>();

    public void clear(Sigil sigil) {
        listeners.remove(sigil);
    }

    public <T extends Event> void addListener(Sigil sigil, Class<T> eventClass, Consumer<T> eventConsumer) {
        listeners.compute(sigil, (sigil1, classListMap) -> {
            if (classListMap == null) {
                classListMap = new HashMap<>();
            }

            classListMap.compute(eventClass, (aClass, consumers) -> {
                if (consumers == null) {
                    consumers = new HashSet<>();
                }

                consumers.add(eventConsumer);
                return consumers;
            });
            return classListMap;
        });

        if (!appliedListeners.contains(eventClass)) {
            try {
                //TODO
                // Maybe use MethodHandles to speed this up?
                Listener proxy = new Listener() {
                    public void eventCallback(Object obj) {
                        listeners.values().stream()
                                .filter(m -> m.containsKey(obj.getClass()))
                                .map(m -> m.get(obj.getClass()))
                                .forEach(consumers -> consumers.forEach(consumer -> consumer.accept(obj)));
                    }
                };
                Method m = proxy.getClass().getDeclaredMethod("eventCallback", Object.class);
                Bukkit.getPluginManager().registerEvent(
                        eventClass,
                        proxy,
                        EventPriority.NORMAL,
                        new MethodHandleEventExecutor(eventClass, m),
                        SpiritSigils.getInstance()
                );

                appliedListeners.add(eventClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeSigil(Sigil sigil) {
        listeners.remove(sigil);
    }
}
