package tio;

import java.io.*;

/**
 * The class <code>ReadException</code> is used to convert
 * java.io.IOExceptions into a subtype of 
 * RuntimeException. By doing this, users of ReadInput
 * methods do not need to use throws declarations,
 * simplifying beginning programs. Subtypes of 
 * RuntimeException do not need to be declared using
 * a throws clause.
 *
 * @author C. E. McDowell
 * @version 1.1, Released for Java By Dissection
 *
 */
public class ReadException extends RuntimeException {

  /**
   * Constructs a ReadException object with no 
   * specific message.
   */
  public ReadException() {
    super();
  }

  /**
   * Constructs a ReadException object with the 
   * specified message.
   *
   * @param message the error message
   */
  public ReadException(String message) {
    super(message);
  }
}
