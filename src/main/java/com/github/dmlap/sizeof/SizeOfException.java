/**
 * 
 */
package com.github.dmlap.sizeof;

/**
 * Thrown by {@link SizeOf} to indicate an error has occurred determining the
 * runtime size of an {@link Object}.
 */
public class SizeOfException extends RuntimeException {
  private static final long serialVersionUID = 1693426990158587765L;

  public SizeOfException(String message, Throwable cause) {
    super(message, cause);
  }
}
