package tio;
import java.io.*;

/**
 * The class <code>Console</code> is a convenience class.
  * It contains a static variable <code>in</code> that is
  * initialized to refer to a ReadInput object, reading
  * from the standard input stream System.in.
  * It also contains a static variable <code>out</code>
  * that is initialized to refer to a FormattedWriter,
  * writing to the output stream System.out.
  */
public class Console {
  public final static ReadInput in = 
        new ReadInput(new InputStreamReader(System.in));
  public final static FormattedWriter out =
        new FormattedWriter(System.out);
}
