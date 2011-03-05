jvm-sizeof is a utility to estimate the heap usage of objects. It's tiny, has no dependencies and is reasonably efficient. It offers methods to compute the shallow size and the retained heap obtained by traversing the object graph of outgoing references.

Usage
=====
jvm-sizeof makes use of the `JVM instrumentation framework<http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html>`_, so it has to be configured as a javaagent and on your classpath when starting your java process::

    java -cp jvm-sizeof-0.1.jar -javaagent:jvm-sizeof-0.1.jar MyClass

After that, it's very simple to calculate the size of objects::

    import static com.github.dmlap.sizeof.SizeOf.sizeof;
    import static com.github.dmlap.sizeof.SizeOf.deepsize;
    
    public class MyClass {
        private String Hello = "hello";
        public static void main(String[] args) {
            System.out.println("Shallow size of \"hello\": " + sizeof("hello"));
            System.out.println("Transitive size of MyClass: " + deepsize(new MyClass())); 
        }
    }
