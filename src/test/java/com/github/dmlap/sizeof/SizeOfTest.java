package com.github.dmlap.sizeof;

import static com.github.dmlap.sizeof.SizeOf.deepsize;

import java.lang.instrument.Instrumentation;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SizeOfTest {
  private static Instrumentation instrumentation;
  private static JUnit4Mockery context = new JUnit4Mockery();
  
  @BeforeClass
  public static void setupInstrumentation() {
    instrumentation = context.mock(Instrumentation.class);
    context.checking(new Expectations() {{
      exactly(8).of(instrumentation).getObjectSize(with(anything()));
    }});
    SizeOf.premain("", instrumentation);
  }
  
  @Test
  public void deepsizeSimple() {
    final Deep deep = new Deep();
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(deep);
      oneOf(instrumentation).getObjectSize(deep.shallow);
    }});
    deepsize(deep);
  }
  
  @Test
  public void deepsizeShouldHandlePrimitiveArrays() {
    final int[] value = new int[] { 0, 1 };
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(value);
    }});
    deepsize(value);
  }
  
  @Test
  public void deepsizeShouldHandleReferenceArrays() {
    final Shallow first = new Shallow();
    final Shallow second = new Shallow();
    final Shallow[] value = new Shallow[] { first, second };
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(value);
      oneOf(instrumentation).getObjectSize(first);
      oneOf(instrumentation).getObjectSize(second);
    }});
    deepsize(value);
  }
  
  @Test
  public void deepsizeShouldNotDoubleCountArrayElementReferences() {
    final Shallow shallow = new Shallow();
    final Shallow[] value = new Shallow[] { shallow, shallow };
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(value);
      oneOf(instrumentation).getObjectSize(shallow);
    }});
    deepsize(value);
  }
  
  @Test
  public void deepsizeShouldNotDoubleCountObjectsReferences() {
    final Shallow elem = new Shallow();
    final Shallow[] value = new Shallow[] { elem, elem };
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(value);
      oneOf(instrumentation).getObjectSize(elem);
    }});
    deepsize(value);
  }
  
  @Test
  public void deepsizeShouldHandleNullFields() {
    final Deep deep = new Deep();
    deep.shallow = null;
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(deep);
    }});
    deepsize(deep);
  }
  
  @Test
  public void deepsizeShouldHandleNullArrayElements() {
    final Object[] array = new Object[] { null };
    context.checking(new Expectations() {{
      oneOf(instrumentation).getObjectSize(array);
    }});
    deepsize(array);
  }
  
  private static class Shallow {
    @SuppressWarnings("unused")
    public int x = 0;
  }
  
  private static class Deep {
    public Shallow shallow = new Shallow();
  }
}
