package org.crosswire.common.activate;

import java.util.HashSet;
import java.util.Set;

public final class Activator {

    public static void activate(Activatable subject) {
        if (!activated.contains(subject) && subject != null) {
            subject.activate(lock);
            activated.add(subject);
        }
    }

    public static void reduceMemoryUsage(Kill amount) {
        amount.reduceMemoryUsage();
    }

    public static void deactivate(Activatable subject) {
        if (activated.contains(subject) && subject != null) {
            subject.deactivate(lock);
            activated.remove(subject);
        }
    }

    public static void deactivateAll() {
        for (Activatable item : activated) {
            deactivate(item);
        }
    }

    private static Set<Activatable> activated = new HashSet<Activatable>();

    private static Lock lock = new Lock();
}
