package tio;

import java.io.*;
import java.text.*;

/**
 * The class <code>FormattedWriter</code> contains
 * methods that allow for formatted printing. 
 * It includes support for setting the width of the
 * output field, using left or right justification in
 * the output field, using an arbitrary fill
 * character, and setting the number of digits to the
 * right of the decimal point in floating point
 * values.
 *
 * @author C. E. McDowell
 * @version 1.2 corrected problem with floats with exponents.
 */
public class FormattedWriter extends PrintWriter {
  // constants for specifying justification
  public static final int LEFT = 1;
  public static final int RIGHT = 2;

  /**
   * Constructs a FormattedWriter object for an
   * OutputStream.
   *
   * @param os the OutputStream to write to
   */
  public FormattedWriter(OutputStream os) {
    super(os, true); // make default auto-flushing
  }

  /**
   * Constructs a FormattedWriter object for a
   * FileWriter.
   *
   * @param writer the FileWriter to write to
   */
  public FormattedWriter(FileWriter writer) {
    super(writer, true);
  }

  /**
   * Constructs a FormattedWriter object for writing
   * to a file.
   *
   * @param filename the name of the file to write to
   */
  public FormattedWriter(String filename)
    throws java.io.IOException
  {
    this(new FileWriter(filename));
  }

  /**
   * Set the output field width. If the value being
   * printed is less than the width of the field, then
   * the field will be padded with the pad character
   * (see setPadChar()). The field can be either left
   * or right justified (see setJustify()).
   *
   * @param width the width of the output field
   */
  public void setWidth(int width) {
    if (width < 0)
      this.width = 0;
    else if (width > MAX_WIDTH)
      this.width = MAX_WIDTH;
    else 
      this.width = width;
  }

  /**
   * Set the number of digits to be printed to the
   * right of the decimal point in floating point
   * values.
   *
   * @param places the number of places to the right
   *    of the decimal point
   */
  public void setDigits(int places) {
    decimalPlaces = places;
    form.setMaximumFractionDigits(decimalPlaces);
  }

  /**
   * Set the justification to be LEFT or RIGHT.
   *
   * @param leftOrRight use FormattedWriter.LEFT 
   *    or FormattedWriter.RIGHT
   * @exception IllegalArgumentException if not LEFT 
   *    or RIGHT
   */
  public void setJustify(int leftOrRight) {
    if (leftOrRight != LEFT && leftOrRight != RIGHT)
      throw new IllegalArgumentException(
        "use FormattedWriter.LEFT or" +
        " FormattedWriter.RIGHT");
    justify = leftOrRight;
  }

  /**
   * Set the character to be used in padding. 
   * The default padding character is a blank.
   *
   * @param pad the character to use in padding
   */
  public void setPadChar(char pad) { 
    if (pad == ' ')
      padding = spaces;
    else if (pad == '0')
      padding = zeros;
    else
      padding = buildPadding(MAX_WIDTH, pad);
  }

  /**
   * Print a String in a field of the current
   * width using the current padding character 
   * and justification.
   *
   * @param s the String to print
   */
  public void printf(String s) {
    if (s.length() >= width)
      super.print(s);
    else if (justify == LEFT)
      super.print(s + 
          padding.substring(0, width - s.length()));
    else
      super.print(
          padding.substring(0, width - s.length()) + s);
  }


  /**
   * Print a boolean in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(boolean value) {
    printf(String.valueOf(value));
  }

  /**
   * Print a char in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(char value) {
    printf(String.valueOf(value));
  }

  /**
   * Print an array of characters in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(char[] value) {
    printf(String.valueOf(value));
  }

  /**
   * Print an int in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(int value) {
    printf(String.valueOf(value));
  }

  /**
   * Print a long in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(long value) {
    printf(String.valueOf(value));
  }

  /**
   * Print any Object in a field of the current 
   * width, using the current padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(Object value) {
    printf(value.toString());
  }

  /**
   * Print a double in a field of the current 
   * width, with the current number of digits to the
   * right of the decimal point and using the current
   * padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(double value) { 
    printf(trimDigits(String.valueOf(value)));
  }

  /**
   * Print a float in a field of the current 
   * width, with the current number of digits to the
   * right of the decimal point and using the current
   * padding character and justification.
   *
   * @param value the value to print
   */
  public void printf(float value) {
    printf(trimDigits(String.valueOf(value)));
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(String s) {
    printf(s);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(boolean value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(char value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(char[] value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(int value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(long value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(Object value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(double value) {
    printf(value);
    println();
  }

  /**
   * Same as printf() with a newline added at the end.
   */
  public void printfln(float value) {
    printf(value);
    println();
  }

  /*
   * Trim the number of digits to the right of the
   * decimal point if there is one.
  */
  private String trimDigits(String value) {
    int places;

    if (decimalPlaces == -1)
      return value;
    int pos = value.indexOf(".");
    int exp = value.indexOf("E");
    if (exp == -1)
      places = value.length() - pos - 1;
    else
      places = exp - pos - 1;
    if (places <= decimalPlaces)
        return value;
    if (exp == -1)
      return round(value);
    else {
	String needsRounding = value.substring(0, exp);
      return round(needsRounding) +
          value.substring(exp);
    }
  }

  /*
   * Round the last digit of s. E.g. 1.2345 would be
   * returned as 1.235 and 1.234 would be returned as
   * 1.23.
   * This is done using a java.text.NumberFormat 
   * object that had its decimal places set in
   * setDigits() above.
   */
  private String round(String s) {
    // form is a java.text.NumberFormat object
    return form.format(Double.valueOf(s).doubleValue());
  }

  /*
   * Create an array of pad characters used for
   * quickly building strings of pad characters
   * by a call to substring (see printfln(String s))
  */
  private static String buildPadding(int width,
                                     char pad)
  {
    StringBuffer sbuf = new StringBuffer(width);
    for (int i = 0; i < width; ++i)
      sbuf.append(pad);
    return sbuf.toString();
  }

  private static int MAX_WIDTH = 40;
  private static final String spaces =
      buildPadding(MAX_WIDTH, ' ');
  private static final String zeros =
      buildPadding(MAX_WIDTH, '0');
  private String padding = spaces;
  private int width = 0;
  private int justify = LEFT;

  // -1 means use max precision
  private int decimalPlaces = -1;
  // used in trimming decimal digits
  private NumberFormat form =
       NumberFormat.getInstance();
}
