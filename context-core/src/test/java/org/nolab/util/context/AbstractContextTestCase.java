package org.nolab.util.context;

import org.nolab.util.context.test.ContextTI;
import org.junit.Test;
import org.nolab.util.context.Context.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.nolab.util.context.Context.ReplaceRule.*;
import static org.junit.Assert.*;

/**
 * Base class for testing {@link Context}.
 * <p>By default supposed {@code null} values are supported.
 *
 * @param <C> the implementation
 */
public abstract class AbstractContextTestCase<C extends Context> {

    /**
     * Get instance of testable context.
     * Context must be empty.
     *
     * @return instance of testable context
     */
    protected abstract C getTestableContext();

    /**
     * Get instance of testable context with desired initial capacity.
     * Context must be empty.
     *
     * @param capacity initial capacity of testable context
     * @return instance of testable context
     */
    protected abstract C getTestableContext(int capacity);

    /**
     * Get instance of testable context with mappings pre-defined
     * in specified map.
     * <p>Context state does non reflect specified map state
     * and visa-versa.
     *
     * @param source map with pre-defined mappings
     * @return instance of testable context
     */
    protected abstract C getTestableContext(Map<String, Object> source);

    /**
     * Get instance context to be used in support operations,
     * for example, data exchange.
     * Context must be empty.
     *
     * @return instance of support context
     */
    protected Context getSupportContext() {
        return new ContextTI(5);
    }

    /**
     * Get instance context to be used in support operations,
     * for example, data exchange, with given initial capacity.
     * Context must be empty.
     *
     * @param capacity initial capacity
     * @return instance of support context
     */
    protected Context getSupportContext(int capacity) {
        return new ContextTI(capacity);
    }

    /**
     * Get instance of map be used in support operations,
     * for example, data exchange.
     * Map must be empty.
     * Must support {@code null} values.
     *
     * @return instance of map
     */
    protected Map<String, Object> getSupportMap() {
        return new HashMap<>();
    }

    protected final String key1 = "key1";
    protected final Integer value1 = 1;
    protected final String key2 = "key2";
    protected final Integer value2 = 2;
    protected final String key3 = "key3";
    protected final Integer value3 = 3;
    protected final String key4 = "key4";
    protected final Integer value4 = 4;
    protected final String key5 = "key5";
    protected final Integer value5 = value1;
    protected final Float value05 = 0.5f;

    protected final BiPredicate<String, Object> allButKey1 = (key, value) ->
        (value instanceof Integer && ((Integer) value) > 1) || (key.equals(key5));

    protected void put123(Context context) {
        context.put(key1, value1);
        context.put(key2, value2);
        context.put(key3, value3);
    }

    protected Map<String, Object> put123(Map<String, Object> map) {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    protected void put12345(Context context) {
        context.put(key1, value1);
        context.put(key2, value2);
        context.put(key3, value3);
        context.put(key4, value4);
        context.put(key5, value5);
    }

    protected Map<String, Object> put12345(Map<String, Object> map) {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return map;
    }

    protected void put123ToEmpty(Context context) {
        assertNull(context.put(key1, value1));
        assertNull(context.put(key2, value2));
        assertNull(context.put(key3, value3));
    }

    protected void assertFull123(Context context) {
        assertTrue(context.containsKey(key1));
        assertTrue(context.containsValue(value1));
        assertEquals(value1, context.get(key1));
        assertTrue(context.containsKey(key2));
        assertTrue(context.containsValue(value2));
        assertEquals(value2, context.get(key2));
        assertTrue(context.containsKey(key3));
        assertTrue(context.containsValue(value3));
        assertEquals(value3, context.get(key3));
    }

    protected void assertFull123WithSize(Context context) {
        assertFalse(context.isEmpty());
        assertEquals(3, context.size());
        assertFull123(context);
    }

    protected void assertFull123(Map<String, Object> map) {
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsValue(value1));
        assertEquals(value1, map.get(key1));
        assertTrue(map.containsKey(key2));
        assertTrue(map.containsValue(value2));
        assertEquals(value2, map.get(key2));
        assertTrue(map.containsKey(key3));
        assertTrue(map.containsValue(value3));
        assertEquals(value3, map.get(key3));
    }

    protected void assertFull123WithSize(Map<String, Object> map) {
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertFull123(map);
    }

    protected void assertFull12(Context context) {
        assertTrue(context.containsKey(key1));
        assertTrue(context.containsValue(value1));
        assertEquals(value1, context.get(key1));
        assertTrue(context.containsKey(key2));
        assertTrue(context.containsValue(value2));
        assertEquals(value2, context.get(key2));
    }

    protected void assertFull12WithSize(Context context) {
        assertFalse(context.isEmpty());
        assertEquals(2, context.size());
        assertFull12(context);
    }

    protected void assertFull12(Map<String, Object> map) {
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsValue(value1));
        assertEquals(value1, map.get(key1));
        assertTrue(map.containsKey(key2));
        assertTrue(map.containsValue(value2));
        assertEquals(value2, map.get(key2));
    }

    protected void assertFull12345(Context context) {
        assertTrue(context.containsKey(key1));
        assertTrue(context.containsValue(value1));
        assertEquals(value1, context.get(key1));
        assertTrue(context.containsKey(key2));
        assertTrue(context.containsValue(value2));
        assertEquals(value2, context.get(key2));
        assertTrue(context.containsKey(key3));
        assertTrue(context.containsValue(value3));
        assertEquals(value3, context.get(key3));
        assertTrue(context.containsKey(key4));
        assertTrue(context.containsValue(value4));
        assertEquals(value4, context.get(key4));
        assertTrue(context.containsKey(key5));
        assertTrue(context.containsValue(value5));
        assertEquals(value5, context.get(key5));
    }

    protected void assertFull12345WithSize(Context context) {
        assertFalse(context.isEmpty());
        assertEquals(5, context.size());
        assertFull12345(context);
    }

    protected void assertFull12345(Map<String, Object> map) {
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsValue(value1));
        assertEquals(value1, map.get(key1));
        assertTrue(map.containsKey(key2));
        assertTrue(map.containsValue(value2));
        assertEquals(value2, map.get(key2));
        assertTrue(map.containsKey(key3));
        assertTrue(map.containsValue(value3));
        assertEquals(value3, map.get(key3));
        assertTrue(map.containsKey(key4));
        assertTrue(map.containsValue(value4));
        assertEquals(value4, map.get(key4));
        assertTrue(map.containsKey(key5));
        assertTrue(map.containsValue(value5));
        assertEquals(value5, map.get(key5));
    }

    protected void assertFull12345WithSize(Map<String, Object> map) {
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        assertFull12345(map);
    }

    protected void assertFull2345(Context context) {
        assertTrue(context.containsKey(key2));
        assertTrue(context.containsValue(value2));
        assertEquals(value2, context.get(key2));
        assertTrue(context.containsKey(key3));
        assertTrue(context.containsValue(value3));
        assertEquals(value3, context.get(key3));
        assertTrue(context.containsKey(key4));
        assertTrue(context.containsValue(value4));
        assertEquals(value4, context.get(key4));
        assertTrue(context.containsKey(key5));
        assertTrue(context.containsValue(value5));
        assertEquals(value5, context.get(key5));
    }

    protected void assertFull2345WithSize(Context context) {
        assertFalse(context.isEmpty());
        assertEquals(4, context.size());
        assertFull2345(context);
    }

    protected void assertFull2345(Map<String, Object> map) {
        assertTrue(map.containsKey(key2));
        assertTrue(map.containsValue(value2));
        assertEquals(value2, map.get(key2));
        assertTrue(map.containsKey(key3));
        assertTrue(map.containsValue(value3));
        assertEquals(value3, map.get(key3));
        assertTrue(map.containsKey(key4));
        assertTrue(map.containsValue(value4));
        assertEquals(value4, map.get(key4));
        assertTrue(map.containsKey(key5));
        assertTrue(map.containsValue(value5));
        assertEquals(value5, map.get(key5));
    }

    protected void assertFull2345WithSize(Map<String, Object> map) {
        assertFalse(map.isEmpty());
        assertEquals(4, map.size());
        assertFull2345(map);
    }

    protected void assertContainsOnly1(Context context) {
        assertFalse(context.isEmpty());
        assertEquals(1, context.size());
        assertTrue(context.containsKey(key1));
        assertTrue(context.containsValue(value1));
        assertEquals(value1, context.get(key1));
        assertFalse(context.containsKey(key2));
        assertFalse(context.containsValue(value2));
        assertNull(context.get(key2));
        assertFalse(context.containsKey(key3));
        assertFalse(context.containsValue(value3));
        assertNull(context.get(key3));
        assertFalse(context.containsKey(key4));
        assertFalse(context.containsValue(value4));
        assertNull(context.get(key4));
        assertFalse(context.containsKey(key5));
        assertNull(context.get(key5));
    }

    protected void assertContainsOnly1(Map<String, Object> map) {
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsValue(value1));
        assertEquals(value1, map.get(key1));
        assertFalse(map.containsKey(key2));
        assertFalse(map.containsValue(value2));
        assertNull(map.get(key2));
        assertFalse(map.containsKey(key3));
        assertFalse(map.containsValue(value3));
        assertNull(map.get(key3));
        assertFalse(map.containsKey(key4));
        assertFalse(map.containsValue(value4));
        assertNull(map.get(key4));
        assertFalse(map.containsKey(key5));
        assertNull(map.get(key5));
    }
    
    protected void assertFullContains(Context context, String key, Object value) {
        assertTrue(context.containsKey(key));
        assertTrue(context.containsValue(value));
        assertEquals(value, context.get(key));
    }

    protected void assertFullContains(Map<String, Object> map, String key, Object value) {
        assertTrue(map.containsKey(key));
        assertTrue(map.containsValue(value));
        assertEquals(value, map.get(key));
    }

    private void assertFullNotContains(Context context, String key, Object value) {
        assertFalse(context.containsKey(key));
        assertFalse(context.containsValue(value));
        assertNull(context.get(key));
    }

    /**
     * Checking throws NPE and contents remains unchanged.
     *
     * @param method method to be checked
     * @param subj context to be checked
     * @param asserts additional asserts
     * @param args method args in declaration order
     */
    protected static void checkNPE(Method method, Context subj, Runnable asserts,
                                   Object... args) {
        Object[] argsWithNull = new Object[args.length];
        System.arraycopy(args, 0, argsWithNull, 0, args.length);
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            if (parameterTypes[i].isPrimitive()) {
                continue;
            }
            NullPointerException npe = null;
            argsWithNull[i] = null;
            try {
                method.invoke(subj, argsWithNull);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t instanceof NullPointerException) {
                    npe = (NullPointerException) t;
                } else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else {
                    throw new RuntimeException(t);
                }
            }
            assertNotNull(npe);
            asserts.run();
            argsWithNull[i] = args[i];
        }
    }

    /**
     * {@link Context#isEmpty()}
     * {@link Context#size()} ()}
     * {@link Context#containsKey(String)} ()}
     * {@link Context#containsValue(Object)} ()}
     */
    @Test
    public void testContentInfoMethods() {

        Map<String, Object> source = getSupportMap();
        source.put(key1, value1);
        source.put(key2, value2);
        source.put(key3, value3);
        C context;

        context = getTestableContext();
        assertTrue(context.isEmpty());
        assertEquals(0, context.size());
        assertFullNotContains(context, key1, value1);

        context = getTestableContext(source);
        assertFull123WithSize(context);
        assertFullNotContains(context, key4, value4);
    }

    /**
     * {@link Context#get(String)}
     * {@link Context#get(String, Class)}
     * {@link Context#getOrDefault(String, Object)}
     * {@link Context#getOrDefault(String, Class, Object)}
     * {@link Context#getOrCompute(String, Function)}
     * {@link Context#getOrCompute(String, Class, Function)}
     */
    @Test
    public void testGetMethods() {

        Map<String, Object> source = getSupportMap();
        source.put(key2, value2);
        source.put(key4, null);
        C context = getTestableContext(source);

        assertEquals(value2, context.get(key2));
        assertNull(context.get(key3));
        assertNull(context.get(key4));

        assertEquals(value2, context.get(key2, Integer.class));
        assertNull(context.get(key3, Integer.class));
        assertNull(context.get(key4, Integer.class));
        assertNull(context.get(key2, Float.class));

        assertEquals(value2, context.getOrDefault(key2, value3));
        assertEquals(value3, context.getOrDefault(key3, value3));
        assertNull(context.getOrDefault(key4, value3));

        assertEquals(value2, context.getOrDefault(key2, Integer.class, value3));
        assertEquals(value3, context.getOrDefault(key3, Integer.class, value3));
        assertEquals(value3, context.getOrDefault(key4, Integer.class, value3));
        assertEquals(value05, context.getOrDefault(key2, Float.class, value05));

        assertEquals(value2, context.getOrCompute(key2, key -> value3));
        assertEquals(value3, context.getOrCompute(key3, key -> value3));
        assertNull(context.getOrCompute(key4, key -> value3));

        assertEquals(value2, context.getOrCompute(key2, Integer.class, key -> value3));
        assertEquals(value3, context.getOrCompute(key3, Integer.class, key -> value3));
        assertEquals(value3, context.getOrCompute(key4, Integer.class, key -> value3));
        assertEquals(value05, context.getOrCompute(key2, Float.class, key -> value05));

        try {
            context.getOrDefault(key2, null, value3);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.getOrCompute(key2, null);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.getOrCompute(key3, null, key -> value3);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.getOrCompute(key3, Integer.class, null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * {@link Context#put(String, Object)}
     * {@link Context#putIfAbsent(String, Object)}
     * {@link Context#putIfKeyAbsent(String, Object)}
     */
    @Test
    public void testPutMethods() {

        C context = getTestableContext(5);

        assertTrue(context.isEmpty());
        assertEquals(0, context.size());
        assertFalse(context.containsKey(key1));
        assertFalse(context.containsValue(value1));
        assertNull(context.get(key1));

        put123ToEmpty(context);
        assertFull123WithSize(context);

        assertEquals(value3, context.put(key3, value4));
        assertFullContains(context, key3, value4);
        assertFalse(context.containsValue(value3));

        assertEquals(value4, context.put(key3, null));
        assertFullContains(context, key3, null);
        assertFalse(context.containsValue(value4));

        assertEquals(value2, context.putIfAbsent(key2, value3));
        assertFullContains(context, key2, value2);
        assertFalse(context.containsValue(value3));

        assertNull(context.putIfAbsent(key3, value3));
        assertFullContains(context, key3, value3);
        assertFalse(context.containsValue(null));

        assertFullNotContains(context, key4, value4);
        assertNull(context.putIfAbsent(key4, value4));
        assertFullContains(context, key4, value4);

        context = getTestableContext(5);
        put123ToEmpty(context);

        assertEquals(value3, context.put(key3, null));
        assertNull(context.putIfKeyAbsent(key3, value3));
        assertTrue(context.containsKey(key3));
        assertTrue(context.containsValue(null));
        assertFalse(context.containsValue(value3));
        assertNull(context.get(key3));

        assertEquals(value2, context.putIfKeyAbsent(key2, value3));
        assertFullContains(context, key2, value2);
        assertFalse(context.containsValue(value3));

        assertFullNotContains(context, key4, value4);
        assertNull(context.putIfKeyAbsent(key4, value4));
        assertFullContains(context, key4, value4);
    }

    /**
     * {@link Context#remove(String)}
     * {@link Context#removeExactly(String, Object)}
     * {@link Context#remove(String, Object)}
     * {@link Context#removeOfType(String, Class)}
     * {@link Context#removeOrGetDefault(String, Object)}
     * {@link Context#removeOrGetDefault(String, Class, Object)}
     * {@link Context#removeOrCompute(String, Function)}
     * {@link Context#removeOrCompute(String, Class, Function)}
     * {@link Context#clear()}
     */
    @Test
    public void testRemoveMethods() {

        C context = getTestableContext(3);

        assertTrue(context.isEmpty());
        assertEquals(0, context.size());

        put123ToEmpty(context);
        assertFull123WithSize(context);

        assertNull(context.remove(key4));
        assertFull123WithSize(context);

        assertEquals(value3, context.remove(key3));
        assertFull12WithSize(context);
        assertFullNotContains(context, key3, value3);

        context.put(key3, null);
        assertNull(context.remove(key3));
        assertFull12WithSize(context);
        assertFullNotContains(context, key3, value3);
        assertFalse(context.containsValue(null));

        assertFalse(context.removeExactly(key2, new Integer(value2)));
        assertFullContains(context, key2, value2);
        assertTrue(context.removeExactly(key2, value2));
        assertFullNotContains(context, key2, value2);
        assertFalse(context.removeExactly(key2, value2));
        assertFullNotContains(context, key2, value2);

        assertTrue(context.remove(key1, value1));
        assertFullNotContains(context, key1, value1);
        assertFalse(context.removeExactly(key1, value1));
        assertFullNotContains(context, key1, value1);

        context = getTestableContext();

        context.put(key2, value2);
        assertNull(context.removeOfType(key3, Integer.class));
        assertNull(context.removeOfType(key2, Float.class));
        assertEquals(value2, context.removeOfType(key2, Integer.class));

        context.put(key2, null);
        assertNull(context.removeOfType(key2, Integer.class));
        assertTrue(context.containsKey(key2));
        assertTrue(context.containsValue(null));
        assertNull(context.get(key2));

        assertNull(context.put(key2, value2));
        assertEquals(value3, context.removeOrGetDefault(key3, value3));
        assertFullContains(context, key2, value2);
        assertEquals(value2, context.removeOrGetDefault(key2, value3));
        assertFullNotContains(context, key2, value2);
        assertEquals(value3, context.removeOrGetDefault(key2, value3));
        assertFullNotContains(context, key2, value2);

        assertNull(context.put(key2, value2));
        assertEquals(value3, context.removeOrGetDefault(key3, Integer.class, value3));
        assertEquals(value05, context.removeOrGetDefault(key2, Float.class, value05));
        assertEquals(value05, context.removeOrGetDefault(key3, Float.class, value05));
        assertFullContains(context, key2, value2);
        assertEquals(value2, context.removeOrGetDefault(key2, Integer.class, value3));
        assertFullNotContains(context, key2, value2);

        assertNull(context.put(key2, value2));
        assertEquals(value3, context.removeOrCompute(key3, key -> value3));
        assertFullContains(context, key2, value2);
        assertEquals(value2, context.removeOrCompute(key2, key -> value3));
        assertFullNotContains(context, key2, value2);
        assertEquals(value3, context.removeOrCompute(key2, key -> value3));

        assertNull(context.put(key2, value2));
        assertEquals(value3, context.removeOrCompute(key3, Integer.class, key -> value3));
        assertEquals(value05, context.removeOrCompute(key2, Float.class, key -> value05));
        assertEquals(value05, context.removeOrCompute(key3, Float.class, key -> value05));
        assertFullContains(context, key2, value2);
        assertEquals(value2, context.removeOrCompute(key2, Integer.class, key -> value3));
        assertFullNotContains(context, key2, value2);

        context = getTestableContext(3);
        put123ToEmpty(context);
        context.clear();
        assertTrue(context.isEmpty());
        assertEquals(0, context.size());
        assertFullNotContains(context, key1, value1);
        assertFullNotContains(context, key2, value2);
        assertFullNotContains(context, key3, value3);

        put123ToEmpty(context);
        assertFullContains(context, key1, value1);
        assertFullContains(context, key2, value2);
        assertFullContains(context, key3, value3);
        assertEquals(value3, context.put(key3, null));
        assertNull(context.putIfKeyAbsent(key3, value3));
        assertTrue(context.containsKey(key3));
        assertTrue(context.containsValue(null));
        assertFalse(context.containsValue(value3));
        assertNull(context.get(key3));

        try {
            context.removeOfType(key3, null);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.removeOrGetDefault(key2, null, value3);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.removeOrCompute(key3, null);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.removeOrCompute(key3, null, key -> value3);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.removeOrCompute(key3, Integer.class, null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * {@link Context#getOrComputeAndPut(String, Function)}
     * {@link Context#getOrComputeAndPut(String, Class, Function)}
     */
    @Test
    public void testGetOrComputeAndPut() {

        C context = getTestableContext(2);

        context.put(key1, value1);
        assertEquals(1, context.size());

        assertNull(context.get(key2));
        assertEquals(value2, context.getOrComputeAndPut(key2, key -> value2));
        assertEquals(value2, context.get(key2));
        assertEquals(value2, context.getOrComputeAndPut(key2, key -> value3));

        context.clear();

        assertNull(context.get(key2));
        assertEquals(value2, context.getOrComputeAndPut(key2, Integer.class, key -> value2));
        assertEquals(value2, context.get(key2, Integer.class));
        assertEquals(value2, context.getOrComputeAndPut(key2, Integer.class, key -> value3));

        try {
            context.getOrComputeAndPut(key2, null);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.getOrComputeAndPut(key2, null, key -> value2);
            fail();
        } catch (NullPointerException e) {
        }
        try {
            context.getOrComputeAndPut(key2, Integer.class, null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * {@link Context#equals(Object)}
     */
    @Test
    public void testEquals() {
        C context = getTestableContext(put123(getSupportMap()));
        Context contextE = getSupportContext();
        put123(contextE);
        assertTrue(context.equals(context));
        assertTrue(context.equals(contextE));
        assertTrue(contextE.equals(context));
        put12345(contextE);
        assertFalse(context.equals(contextE));
        assertFalse(contextE.equals(context));
    }

    /**
     * {@link Context#copy()}
     * {@link Context#copy(BiPredicate)}
     */
    @Test
    public void testCopy() {

        C context;
        Context copy;
        Map<String, Object> source = getSupportMap();
        put12345(source);

        context = getTestableContext(source);
        copy = context.copy();
        assertNotSame(context, copy);
        assertFull12345WithSize(copy);
        assertFull12345WithSize(context);
        copy.remove(key1);
        assertFull12345WithSize(context);

        context = getTestableContext(source);
        copy = context.copy(allButKey1);
        assertNotSame(context, copy);
        assertFull12345WithSize(context);
        assertFull2345WithSize(copy);
        assertNull(copy.get(key1));
        assertFalse(copy.containsKey(key1));
        copy.remove(key2);
        assertFull12345WithSize(context);

        try {
            context.copy(null);
            fail();
        } catch (NullPointerException e) {
        }

        context = getTestableContext();
        try {
            context.copy(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * {@link Context#filter(BiPredicate)}
     */
    @Test
    public void testFilter() {

        C context1;
        Context context2;

        context1 = getTestableContext(5);
        put12345(context1);
        context2 = context1.filter(allButKey1);
        assertSame(context1, context2);
        assertFull2345WithSize(context2);
        assertNull(context2.get(key1));
        assertFalse(context2.containsKey(key1));

        try {
            context1.filter(null);
            assertFull12345WithSize(context2);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * {@link Context#copyTo(Context)}
     * {@link Context#copyTo(Context, ReplaceRule)}
     */
    @Test
    public void testCopyToContext() {

        C context = getTestableContext(put123(getSupportMap()));
        Context acceptor = getSupportContext(3);

        context.copyTo(acceptor);
        assertFull123WithSize(acceptor);
        assertFull123WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, PUT);
        assertFull123WithSize(acceptor);
        assertFull123WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, PUT_IF_ABSENT);
        assertFull12(acceptor);
        assertFullContains(acceptor, key3, value4);
        assertFull123WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, PUT_IF_KEY_ABSENT);
        assertFullContains(acceptor, key1, value1);
        assertFullContains(acceptor, key2, null);
        assertFullContains(acceptor, key3, value4);
        assertFull123WithSize(context);

        acceptor.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyTo", Context.class),
                    context, asserts,
                    acceptor);
            checkNPE(Context.class.getDeclaredMethod("copyTo", Context.class, ReplaceRule.class),
                    context, asserts,
                    acceptor, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.copyTo(context);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.copyTo(context, PUT);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#copyTo(Context, BiPredicate)}
     * {@link Context#copyTo(Context, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testCopyToContextWithCriteria() {

        C context = getTestableContext(put12345(getSupportMap()));
        Context acceptor = getSupportContext(5);

        context.copyTo(acceptor, allButKey1);
        assertFull2345WithSize(acceptor);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull12345WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, PUT, allButKey1);
        assertFull2345WithSize(acceptor);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull12345WithSize(context);

        acceptor.clear();
        acceptor.put(key2, value1);
        acceptor.put(key3, null);
        context.copyTo(acceptor, PUT_IF_ABSENT, allButKey1);
        assertFullContains(acceptor, key2, value1);
        assertFullContains(acceptor, key3, value3);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull12345WithSize(context);

        acceptor.clear();
        acceptor.put(key2, value1);
        acceptor.put(key3, null);
        context.copyTo(acceptor, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(acceptor, key2, value1);
        assertFullContains(acceptor, key3, null);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull12345WithSize(context);

        acceptor.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyTo", Context.class,
                    BiPredicate.class),
                    context, asserts,
                    acceptor, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("copyTo", Context.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts, acceptor, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.copyTo(context, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.copyTo(context, PUT, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#drainTo(Context)}
     * {@link Context#drainTo(Context, ReplaceRule)}
     */
    @Test
    public void testDrainToContext() {

        int cap = 3;
        C context = getTestableContext();
        Context acceptor = getSupportContext(cap);

        context.clear();

        put123(context);
        context.drainTo(acceptor);
        assertFull123WithSize(acceptor);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, PUT);
        assertFull123WithSize(acceptor);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, PUT_IF_ABSENT);
        assertFull12(acceptor);
        assertFullContains(acceptor, key3, value4);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, PUT_IF_KEY_ABSENT);
        assertFullContains(acceptor, key1, value1);
        assertFullContains(acceptor, key2, null);
        assertFullContains(acceptor, key3, value4);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainTo", Context.class),
                    context, asserts,
                    acceptor);
            checkNPE(Context.class.getDeclaredMethod("drainTo", Context.class, ReplaceRule.class),
                    context, asserts,
                    acceptor, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.drainTo(context);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.drainTo(context, PUT);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#drainTo(Context, BiPredicate)}
     * {@link Context#drainTo(Context, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testDrainToContextWithCriteria() {

        int cap = 3;
        C context = getTestableContext(cap);
        Context acceptor = getSupportContext(cap);

        context.clear();

        put12345(context);
        context.drainTo(acceptor, allButKey1);
        assertFull2345WithSize(acceptor);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, PUT, allButKey1);
        assertFull2345WithSize(acceptor);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        acceptor.put(key2, value1);
        acceptor.put(key3, null);
        context.drainTo(acceptor, PUT_IF_ABSENT, allButKey1);
        assertFullContains(acceptor, key2, value1);
        assertFullContains(acceptor, key3, value3);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        acceptor.put(key2, value1);
        acceptor.put(key3, null);
        context.drainTo(acceptor, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(acceptor, key2, value1);
        assertFullContains(acceptor, key3, null);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainTo", Context.class,
                    BiPredicate.class),
                    context, asserts, acceptor, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("drainTo", Context.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts, acceptor, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.drainTo(context, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.drainTo(context, PUT, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#copyFrom(Context)}
     * {@link Context#copyFrom(Context, ReplaceRule)}
     */
    @Test
    public void testCopyFromContext() {

        int cap = 3;
        C context = getTestableContext();
        Context source = getSupportContext(cap);

        context.clear();

        put123(source);

        context.copyFrom(source);
        assertFull123WithSize(context);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT);
        assertFull123WithSize(context);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT_IF_ABSENT);
        assertFull12(context);
        assertFullContains(context, key3, value4);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT_IF_KEY_ABSENT);
        assertFullContains(context, key1, value1);
        assertFullContains(context, key2, null);
        assertFullContains(context, key3, value4);
        assertFull123WithSize(source);

        context.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Context.class),
                    context,
                    asserts, source);
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Context.class, ReplaceRule.class),
                    context, asserts,
                    source, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.copyFrom(context);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.copyFrom(context, PUT);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#copyFrom(Context, BiPredicate)}
     * {@link Context#copyFrom(Context, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testCopyFromContextWithCriteria() {

        int cap = 3;
        Context source = getSupportContext();
        C context = getTestableContext(cap);

        context.clear();

        put12345(source);

        context.copyFrom(source, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.copyFrom(source, PUT_IF_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, value3);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.copyFrom(source, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, null);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Context.class,
                    BiPredicate.class),
                    context, asserts,
                    source, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Context.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts, source, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.copyFrom(context, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.copyFrom(context, PUT, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#drainFrom(Context)}
     * {@link Context#drainFrom(Context, ReplaceRule)}
     */
    @Test
    public void testDrainFromContext() {

        int cap = 3;
        C context = getTestableContext(cap);
        Context source = getSupportContext(cap);

        context.clear();

        put123(source);
        context.drainFrom(source);
        assertFull123WithSize(context);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT);
        assertFull123WithSize(context);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT_IF_ABSENT);
        assertFull12(context);
        assertFullContains(context, key3, value4);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT_IF_KEY_ABSENT);
        assertFullContains(context, key1, value1);
        assertFullContains(context, key2, null);
        assertFullContains(context, key3, value4);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Context.class),
                    context, asserts,
                    source);
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Context.class, ReplaceRule.class),
                    context, asserts,
                    source, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.drainFrom(context);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.drainFrom(context, PUT);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#drainFrom(Context, BiPredicate)}
     * {@link Context#drainFrom(Context, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testDrainFromContextWithCriteria() {

        int cap = 3;
        Context source = getSupportContext(cap);
        C context = getTestableContext(cap);

        context.clear();

        put12345(source);
        context.drainFrom(source, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.drainFrom(source, PUT_IF_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, value3);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.drainFrom(source, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, null);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Context.class,
                    BiPredicate.class),
                    context, asserts,
                    source, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Context.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts,
                    source, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        IllegalArgumentException iae = null;
        try {
            context.copyFrom(context, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try {
            context.copyFrom(context, PUT, allButKey1);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * {@link Context#copyTo(Map)}
     * {@link Context#copyTo(Map, boolean)}
     */
    @Test
    public void testCopyToMap() {

        C context = getTestableContext(put123(getSupportMap()));
        Map<String, Object> acceptor = getSupportMap();

        context.copyTo(acceptor);
        assertFull123WithSize(acceptor);
        assertFull123WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, true);
        assertFull123WithSize(acceptor);
        assertFull123WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, false);
        assertFull12(acceptor);
        assertFullContains(acceptor, key3, value4);
        assertFull123WithSize(context);

        acceptor.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyTo", Map.class),
                    context, asserts,
                    acceptor);
            checkNPE(Context.class.getDeclaredMethod("copyTo", Map.class, boolean.class),
                    context, asserts,
                    acceptor, true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#copyTo(Map, BiPredicate)}
     * {@link Context#copyTo(Map, boolean, BiPredicate)}
     */
    @Test
    public void testCopyToMapWithCriteria() {

        C context = getTestableContext(put12345(getSupportMap()));
        Map<String, Object> acceptor = getSupportMap();

        context.copyTo(acceptor, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull2345WithSize(acceptor);
        assertFull12345WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, true, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull2345WithSize(acceptor);
        assertFull12345WithSize(context);

        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.copyTo(acceptor, false, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFullContains(acceptor, key2, value2);
        assertFullContains(acceptor, key3, value4);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertFull12345WithSize(context);

        acceptor.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyTo", Map.class,
                    BiPredicate.class),
                    context, asserts,
                    acceptor, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("copyTo", Map.class, boolean.class,
                    BiPredicate.class),
                    context, asserts,
                    acceptor, true, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#drainTo(Map)}
     * {@link Context#drainTo(Map, boolean)}
     */
    @Test
    public void testDrainToMap() {

        C context = getTestableContext(3);
        Map<String, Object> acceptor = getSupportMap();

        context.clear();

        put123(context);
        context.drainTo(acceptor);
        assertFull123WithSize(acceptor);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, true);
        assertFull123WithSize(acceptor);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, false);
        assertFull12(acceptor);
        assertFullContains(acceptor, key3, value4);
        assertTrue(context.isEmpty());

        put123(context);
        acceptor.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainTo", Map.class),
                    context, asserts,
                    acceptor);
            checkNPE(Context.class.getDeclaredMethod("drainTo", Map.class, boolean.class),
                    context, asserts,
                    acceptor, true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#drainTo(Map, BiPredicate)}
     * {@link Context#drainTo(Map, boolean, BiPredicate)}
     */
    @Test
    public void testDrainToMapWithCriteria() {

        C context = getTestableContext(5);
        Map<String, Object> acceptor = getSupportMap();

        context.clear();

        put12345(context);
        context.drainTo(acceptor, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull2345WithSize(acceptor);
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, true, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFull2345WithSize(acceptor);
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        acceptor.put(key2, null);
        acceptor.put(key3, value4);
        context.drainTo(acceptor, false, allButKey1);
        assertFalse(acceptor.containsKey(key1));
        assertNull(acceptor.get(key1));
        assertFullContains(acceptor, key2, value2);
        assertFullContains(acceptor, key3, value4);
        assertFullContains(acceptor, key4, value4);
        assertFullContains(acceptor, key5, value5);
        assertContainsOnly1(context);

        put12345(context);
        acceptor.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(context);
            assertTrue(acceptor.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainTo", Map.class,
                    BiPredicate.class),
                    context, asserts,
                    acceptor, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("drainTo", Map.class, boolean.class,
                    BiPredicate.class),
                    context, asserts,
                    acceptor, true, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#copyFrom(Map)}
     * {@link Context#copyFrom(Map, ReplaceRule)}
     */
    @Test
    public void testCopyFromMap() {

        C context = getTestableContext(3);
        Map<String, Object> source = getSupportMap();

        context.clear();

        put123(source);

        context.copyFrom(source);
        assertFull123WithSize(context);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT);
        assertFull123WithSize(context);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT_IF_ABSENT);
        assertFull12(context);
        assertFullContains(context, key3, value4);
        assertFull123WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT_IF_KEY_ABSENT);
        assertFullContains(context, key1, value1);
        assertFullContains(context, key2, null);
        assertFullContains(context, key3, value4);
        assertFull123WithSize(source);

        context.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Map.class),
                    context,
                    asserts, source);
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Map.class, ReplaceRule.class),
                    context, asserts,
                    source, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#copyFrom(Map, BiPredicate)}
     * {@link Context#copyFrom(Map, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testCopyFromMapWithCriteria() {

        C context = getTestableContext(3);
        Map<String, Object> source = getSupportMap();

        context.clear();

        put12345(source);

        context.copyFrom(source, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.copyFrom(source, PUT, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.copyFrom(source, PUT_IF_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, value3);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.copyFrom(source, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, null);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertFull12345WithSize(source);

        context.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Map.class,
                    BiPredicate.class),
                    context, asserts,
                    source, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("copyFrom", Map.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts, source, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#drainFrom(Map)}
     * {@link Context#drainFrom(Map, ReplaceRule)}
     */
    @Test
    public void testDrainFromMap() {

        C context = getTestableContext(3);
        Map<String, Object> source = getSupportMap();

        context.clear();

        put123(source);
        context.drainFrom(source);
        assertFull123WithSize(context);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT);
        assertFull123WithSize(context);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT_IF_ABSENT);
        assertFull12(context);
        assertFullContains(context, key3, value4);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT_IF_KEY_ABSENT);
        assertFullContains(context, key1, value1);
        assertFullContains(context, key2, null);
        assertFullContains(context, key3, value4);
        assertTrue(source.isEmpty());

        put123(source);
        context.clear();
        Runnable asserts = () -> {
            assertFull123WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Map.class),
                    context, asserts,
                    source);
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Map.class, ReplaceRule.class),
                    context, asserts,
                    source, PUT);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@link Context#drainFrom(Map, BiPredicate)}
     * {@link Context#drainFrom(Map, ReplaceRule, BiPredicate)}
     */
    @Test
    public void testDrainFromMapWithCriteria() {

        C context = getTestableContext(3);
        Map<String, Object> source = getSupportMap();

        context.clear();

        put12345(source);
        context.drainFrom(source, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, null);
        context.put(key3, value4);
        context.drainFrom(source, PUT, allButKey1);
        assertFull2345WithSize(context);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.drainFrom(source, PUT_IF_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, value3);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        context.put(key2, value1);
        context.put(key3, null);
        context.drainFrom(source, PUT_IF_KEY_ABSENT, allButKey1);
        assertFullContains(context, key2, value1);
        assertFullContains(context, key3, null);
        assertFullContains(context, key4, value4);
        assertFullContains(context, key5, value5);
        assertFalse(context.containsKey(key1));
        assertNull(context.get(key1));
        assertContainsOnly1(source);

        put12345(source);
        context.clear();
        Runnable asserts = () -> {
            assertFull12345WithSize(source);
            assertTrue(context.isEmpty());
        };
        try {
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Map.class,
                    BiPredicate.class),
                    context, asserts,
                    source, allButKey1);
            checkNPE(Context.class.getDeclaredMethod("drainFrom", Map.class, ReplaceRule.class,
                    BiPredicate.class),
                    context, asserts,
                    source, PUT, allButKey1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected C immutableTestForEach() {

        C context = getTestableContext(put12345(getSupportMap()));
        Map<String, Object> map = getSupportMap();

        context.forEach(map::put);
        assertFull12345WithSize(map);
        map.clear();

        context.forEach(allButKey1, map::put);
        assertFull2345WithSize(map);

        return context;
    }

    /**
     * {@link Context#forEach(BiConsumer)}
     * {@link Context#forEach(BiPredicate, BiConsumer)}
     */
    @Test
    public void testForEach() {

        C context = immutableTestForEach();
        Map<String, Object> map = getSupportMap();

        context.clear();
        context.forEach(allButKey1, map::put);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testToString() {
        Map<String, Object> map = put12345(getSupportMap());
        C context = getTestableContext(map);
        String string = context.toString();
        assertNotNull(string);
    }

    //is checking serialization needed
    protected boolean serializationCheck = true;

    /**
     * Serialize and deserialize original context.
     *
     * @param original original context
     * @return serialized-deserialized context
     */
    @SuppressWarnings("unchecked")
    public C serialTransform(C original) {

        byte[] binary;
        C copy;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput oo = new ObjectOutputStream(bos)) {
            oo.writeObject(original);
            binary = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (ObjectInput oi = new ObjectInputStream(new ByteArrayInputStream(binary))) {
            copy = (C) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return copy;
    }

    protected void shortImmutable12345Check(Context context) {

        Map<String, Object> map = getSupportMap();

        context.copyTo(map);
        assertFull12345WithSize(map);
        assertFull12345WithSize(context);
        map.clear();

        context.copyTo(map, allButKey1);
        assertFalse(map.containsKey(key1));
        assertNull(map.get(key1));
        assertFull2345WithSize(map);
        assertFull12345WithSize(context);
        map.clear();

        context.forEach(map::put);
        assertFull12345WithSize(map);
        map.clear();

        context.forEach(allButKey1, map::put);
        assertFull2345WithSize(map);
    }

    /**
     * Check serialization and immutable operations.
     *
     * @return context for further checking
     */
    protected C doImmutableSerializationChecks() {

        Map<String, Object> map = put12345(getSupportMap());
        C context = getTestableContext(map);
        C serial = serialTransform(getTestableContext(map));

        assertFull12345WithSize(serial);
        assertTrue(context.equals(serial));
        assertTrue(serial.equals(context));

        shortImmutable12345Check(serial);

        Context copy = serial.copy();

        assertFull12345WithSize(copy);
        assertTrue(serial.equals(copy));
        assertTrue(copy.equals(serial));
        assertTrue(context.equals(copy));
        assertTrue(copy.equals(context));

        shortImmutable12345Check(copy);

        return serial;
    }

    protected void shortMutable12345Checks(Context context) {

        Map<String, Object> map = getSupportMap();

        put123(context);
        context.drainTo(map);
        assertFull12345WithSize(map);
        assertTrue(context.isEmpty());

        context.copyFrom(map);
        assertFull12345WithSize(context);
        assertFull12345WithSize(map);
    }

    @Test
    public void testSerialization() {

        if (!serializationCheck) {
            return;
        }

        C serial = doImmutableSerializationChecks();

        put12345(serial);
        assertFull12345WithSize(serial);

        serial.remove(key1);
        assertFull2345WithSize(serial);

        serial.clear();
        assertTrue(serial.isEmpty());

        put12345(serial);
        assertFull12345WithSize(serial);

        shortMutable12345Checks(serial);

        Context copy = serial.copy();

        shortMutable12345Checks(copy);
    }
}
