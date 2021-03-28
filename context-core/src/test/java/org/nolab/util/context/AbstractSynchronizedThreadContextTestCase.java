package org.nolab.util.context;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.*;

import static org.junit.Assert.*;

/**
 * Tests for working with synchronized context in several threads.
 *
 * @param <C> synchronized implementation
 */
public abstract class AbstractSynchronizedThreadContextTestCase<C extends Context>
        extends AbstractContextTestCase<C> {

    @Test
    public void testSyncForEach() throws Throwable {

        C context = getTestableContext();
        Map<String, Object> map = getSupportMap();
        AtomicBoolean finish = new AtomicBoolean(false);

        Thread thr1 = new Thread(() -> {
            while (!finish.get()) {
                context.getOrComputeAndPut(key1, s -> value1);
                context.getOrComputeAndPut(key2, s -> value2);
                context.getOrComputeAndPut(key3, s -> value3);
                put12345(context);
                context.forEach((k, v) -> {
                    assertNotNull(k);
                    assertNotNull(v);
                });
                context.remove(key5);
                context.remove(key4);
                context.getOrComputeAndPut(key4, s -> value4);
                context.getOrComputeAndPut(key5, s -> value5);
                context.remove(key5);
                context.remove(key4);
            }
        });
        AtomicReference<Throwable> err1 = new AtomicReference<>();
        thr1.setUncaughtExceptionHandler((t, e) -> err1.set(e));

        Thread thr2 = new Thread(() -> {
            put123(context);
            context.clear();
            put123(context);
            context.forEach(
                    (k, v) -> !k.equals(key4) && !k.equals(key5),
                    map::put
            );
            finish.set(true);
            assertFull123(context);
            assertFull123WithSize(map);
            finish.set(true);
        });
        AtomicReference<Throwable> err2 = new AtomicReference<>();
        thr2.setUncaughtExceptionHandler((t, e) -> err2.set(e));

        thr1.start();
        thr2.start();

        thr2.join();
        thr1.join();

        Throwable err;

        err = err1.get();
        if (err != null) {
            throw err;
        }
        err = err2.get();
        if (err != null) {
            throw err;
        }
    }

    @Test
    public void testSyncCopy() throws Throwable {

        C context = getTestableContext();
        AtomicBoolean finish = new AtomicBoolean(false);

        Thread thr1 = new Thread(() -> {
            while (!finish.get()) {
                context.getOrComputeAndPut(key1, s -> value1);
                context.getOrComputeAndPut(key2, s -> value2);
                context.getOrComputeAndPut(key3, s -> value3);
                put12345(context);
                context.forEach((k, v) -> {
                    assertNotNull(k);
                    assertNotNull(v);
                });
                context.remove(key5);
                context.remove(key4);
                context.getOrComputeAndPut(key4, s -> value4);
                context.getOrComputeAndPut(key5, s -> value5);
                context.remove(key5);
                context.remove(key4);
            }
        });
        AtomicReference<Throwable> err1 = new AtomicReference<>();
        thr1.setUncaughtExceptionHandler((t, e) -> err1.set(e));

        Thread thr2 = new Thread(() -> {
            put123(context);
            context.clear();
            put123(context);
            assertFull123WithSize(context.copy((k, v) -> !k.equals(key4) && !k.equals(key5)));
            finish.set(true);
        });
        AtomicReference<Throwable> err2 = new AtomicReference<>();
        thr2.setUncaughtExceptionHandler((t, e) -> err2.set(e));

        thr1.start();
        thr2.start();

        thr2.join();
        thr1.join();

        Throwable err;

        err = err1.get();
        if (err != null) {
            throw err;
        }
        err = err2.get();
        if (err != null) {
            throw err;
        }
    }
}
