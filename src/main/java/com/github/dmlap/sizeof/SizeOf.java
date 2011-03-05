package com.github.dmlap.sizeof;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class SizeOf {
  private static final Logger LOG = Logger
      .getLogger("com.github.dmlap.sizeof.SizeOf");
  private static final Map<Class<?>, Long> TYPES = new HashMap<Class<?>, Long>();
  private static Instrumentation instrumentation;

  /**
   * Initialization method called by the JVM at startup.
   * 
   * @see http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html
   */
  public static void premain(String args, Instrumentation inst) {
    assert instrumentation == null : "SizeOf should not be re-initialized.";
    assert inst != null : "SizeOf must be initialized with non-null instrumentation. Make sure you've configured javaagent correctly";
    SizeOf.instrumentation = inst;
    TYPES.put(boolean.class, sizeof(true));
    TYPES.put(byte.class, sizeof((byte) 0));
    TYPES.put(short.class, sizeof((short) 0));
    TYPES.put(int.class, sizeof(0));
    TYPES.put(long.class, sizeof(0L));
    TYPES.put(float.class, sizeof(0.0F));
    TYPES.put(double.class, sizeof(0.0D));
    TYPES.put(char.class, sizeof('a'));
    LOG.info("-- sizeof loaded --");
  }

  /**
   * Returns an implementation-specific approximation of the amount of storage
   * consumed by the specified {@link Object}.
   * 
   * @param target
   *          - the {@link Object} to query
   * @return an implementation-specific approximation of the amount of storage
   *         used
   */
  public static long sizeof(Object target) {
    assert instrumentation != null : "SizeOf has not been initialized. Add it as a javaagent when starting your JVM";
    return instrumentation.getObjectSize(target);
  }

  /**
   * Returns an implementation-specific approximation of the amount of storage
   * consumed by the specified object and all values referenced by it.
   * 
   * @param target
   *          - the {@link Object} to query
   * @return an approximation of the retained heap usage for the {@link Object}
   */
  public static long deepsize(Object target) {
    long result = sizeof(target);
    IdentityHashMap<Object, Void> references = new IdentityHashMap<Object, Void>();
    references.put(target, null);
    IdentityHashMap<Object, Void> unprocessed = new IdentityHashMap<Object, Void>();
    unprocessed.put(target, null);
    do {
      Iterator<Object> itr = unprocessed.keySet().iterator();
      Object node = itr.next();
      itr.remove();
      Class<?> nodeClass = node.getClass();
      if (nodeClass.isArray()) {
        if (node.getClass().getComponentType().isPrimitive()) {
          continue;
        }
        int length = Array.getLength(node);
        for (int i = 0; i < length; ++i) {
          Object elem = Array.get(node, i);
          if (elem == null) {
            continue;
          }
          if (references.containsKey(elem)) {
            continue;
          }
          unprocessed.put(elem, null);
          references.put(elem, null);
          result += sizeof(elem);
        }
        continue;
      }
      for (Field field : nodeClass.getDeclaredFields()) {
        field.setAccessible(true);
        try {
          Class<?> type = field.getType();
          // primitive types
          if(TYPES.containsKey(type)) {
            result += TYPES.get(type);
            continue;
          }
          // reference types
          Object value = field.get(node);
          if(value == null) {
            continue;
          }
          if(references.containsKey(value)) {
            continue;
          }
          unprocessed.put(value, null);
          references.put(value, null);
          result += sizeof(value);
        } catch (IllegalArgumentException e) {
          throw new SizeOfException("Error determing the size of field "
                                    + field.getName() + " on " + target.getClass().getSimpleName(), e);
        } catch (IllegalAccessException e) {
          throw new SizeOfException("Error determing the size of field "
                                    + field.getName() + " on " + target.getClass().getSimpleName(), e);
        }
      }
    } while(!unprocessed.isEmpty());
    return result;
  }
}
