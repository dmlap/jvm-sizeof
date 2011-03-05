package com.github.dmlap.sizeof;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class SizeOf {
    private static final Logger Log = Logger.getLogger("com.github.dmlap.sizeof.SizeOf");
    private static Instrumentation instrumentation;

    /**
     * Initialization method called by the JVM at startup.
     * @see http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html
     */
    public static void premain(String args, Instrumentation intrumentation) {
        assert instrumentation == null : "SizeOf should not be re-initialized.";
        assert instrumentation != null : "SizeOf must be initialized with non-null instrumentation. Make sure you've configured javaagent correctly";
        SizeOf.instrumentation = instrumentation;
        Log.info("-- SizeOf Loaded --");
    }
    /**
     * Returns an implementation-specific approximation of the amount of storage
     * consumed by the specified Object.
     * @param target - the Object to query
     * @return an implementation-specific approximation of the amount of storage
     */
    public static long apply(Object target) {
        assert instrumentation != null : "SizeOf has not been initialized. Add it as a javaagent when starting your JVM";
        return instrumentation.getObjectSize(target);
    }
}
