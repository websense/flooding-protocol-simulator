package tio;

import java.io.*;

/**
 * The class <code>PrintFileWriter</code> is a 
 * convenience class. It adds one constructor to its
 * parent class, PrintWriter. This new constructor
 * takes the name of a file.
 * <p>
 * <code>new PrintFileWriter(fileName)</code> is the
 * same as <code>
 * new PrintWriter(new FileWriter(fileName))
 * </code>.
 */
public class PrintFileWriter extends PrintWriter {
  public PrintFileWriter(String filename)
     throws IOException 
  {
    super(new FileWriter(filename));
  }
}
