package tio;

import java.io.*;

/**
 * The class <code>ReadInput</code> contains methods that
 * allow for simple input of numbers, strings and 
 * characters from a text stream.
 *
 * @author C. E. McDowell
 * @version 1.1, release for Java By Dissection
 */
public class ReadInput {

  /**
   * Constructs a ReadInput object for reading from any
   * Reader object.
   *
   * @param input the Reader text stream to read from. 
   */
  public ReadInput(Reader input) {
    // can look ahead over 1024 white space characters
    // when checking for the end of file mark
    this.input = new PushbackReader(input, 1024);
  }

  /**
   * Constructs a ReadInput object for reading from a
   * file.
   *
   * @param filename the name of the file from which 
   *    to read.
   * @exception FileNotFoundException if the file can't
   *    be opened.
   */
  public ReadInput(String filename) {
    try {
      FileReader fin = new FileReader(filename);
      this.input = new PushbackReader(fin, 1024);
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Constructs a ReadInput object for reading from any
   * InputStream.
   *
   * @param input the InputStream to read from. 
   */
  public ReadInput(InputStream input) {
    this(new InputStreamReader(input));
  }

  /**
   * Check to see if there are any non-white space
   * characters left in the input. If used to terminate
   * reading with readLine(), any trailing blank lines
   * will be ignored. To read trailing blank lines, do
   * not use hasMoreElements() and instead read with
   * readLine() until an EOFException is thrown.
   *
   * @return true if the input contains more non-white
   *     space characters and false otherwise.
   */
  public boolean hasMoreElements() {
    try {
      if (atEof)
        return false;
      else if (whiteSpaceBuffered)
        return true;//something followed the white space
      // look ahead to see if any non-white remain
      int nextChar = input.read();
      if (Character.isWhitespace((char)nextChar)) {
        // save white space in case readLine() comes next
        whiteSpaceBuffered = true;
        buffer[0] = (char)nextChar;
        for (bufferCount = 1; bufferCount < 1024; 
             ++bufferCount)
        {
          nextChar = input.read();
          if (nextChar == -1) {
            atEof = true;
            break;
          }
          else if 
            (!Character.isWhitespace((char)nextChar))
          {
            input.unread(nextChar);
            break;
          }
          buffer[bufferCount] = (char)nextChar;
        }//end for
      }
      else if (nextChar == -1) {
        atEof = true;
        input.unread(nextChar);
      }
      else
        input.unread(nextChar);
      return !atEof;
    }
    catch(java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Read the next character. White space is not skipped.
   * readChar() cannot be used to reread input characters
   * that resulted in a NumberFormatException trying
   * to read a number. readLine() will return the
   * characters of a failed number read.
   *
   * @return the int value of the next character.
   */
  public int readChar() {
    try {
      int result;
      // tokenRead will be true if a token was read
      // but couldn't be parsed. readChar() cannot
      // be used to reread such a token, discard it.
      tokenRead = false;
      if (whiteSpaceBuffered) {
        input.unread(buffer, 0, bufferCount);
        whiteSpaceBuffered = false;
      }
      result = input.read();
      if (result == -1)
        atEof = true;
      return result;
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Attempt to interpret the next white space delimited
   * input characters as a double.
   *
   * @return the double value of the next, white-space 
   *     delimited input string.
   * @exception NumberFormatException if the next input
   *     string does not contain a parsable double.
   */
  public double readDouble() {
    try {
      readToken();
      double result = Double.valueOf(token).doubleValue();
      tokenRead = false; // this token has been used up
      return result;
    }
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Attempt to interpret the next white space delimited
   * input characters as a float.
   *
   * @return the float value of the next, white-space 
   *     delimited input string.
   * @exception NumberFormatException if the next input
   *     string does not contain a parsable float.
   */
  public float readFloat() {
    try {
      readToken();
      float result = Float.valueOf(token).floatValue();
      tokenRead = false; // this token has been used up
      return result;
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Attempt to interpret the next white space delimited
   * input characters as an int.
   *
   * @return the int value of the next, white-space 
   *     delimited input string.
   * @exception NumberFormatException if the next input
   *     string does not contain a parsable int.
   */
  public int readInt() {
    try {
      readToken();
      int result = Integer.parseInt(token);
      tokenRead = false; // this token has been used up
      return result;
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Read the next complete input line up to the newline
   * character. The terminating newline character is read
   * and discarded. It is not part of the return string.
   * If the previous read was an attempt to read a number
   * that generated a NumberFormatException, readLine()
   * will return the input line including the input
   * characters that caused the exception. This can be
   * used to try and recover from failure to read numeric
   * input.
   *
   * @return the next input line as a String.
   */
  public String readLine() {
    try {
      if (tokenRead) {
        tokenRead = false;
        return token + readLine(input);
      }
      else {
        return readLine(input);
      }
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Attempt to interpret the next white space delimited
   * input characters as a long.
   *
   * @return the long value of the next, white-space 
   *     delimited input string.
   * @exception NumberFormatException if the next input
   *     string does not contain a parsable long.
   */
  public long readLong() {
    try {
      readToken();
      long result = Long.parseLong(token);
      tokenRead = false; // this token has been used up
      return result;
    } 
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Read the next white space delimited string.
   *
   * @return the next, white-space delimited input string.
   */
  public String readWord() {
    try {
      readToken();
      tokenRead = false; // this token has been used up
      return token;
    }
    catch (java.io.IOException e) {
      throw new ReadException(e.toString());
    }
  }

  /**
   * Do the work of reading a line of text.
   * White space may have been buffered up from a
   * call to hasMoreElements(). If so, unread the
   * buffered white space then read one line.
   */
  private String readLine(PushbackReader in) 
          throws IOException
  {
    StringBuffer result = new StringBuffer(80);
    if (whiteSpaceBuffered) {
      in.unread(buffer, 0, bufferCount);
      whiteSpaceBuffered = false;
    }
    int nextChar = in.read();
    while (nextChar != -1 && nextChar != '\n' &&
          nextChar != '\r') {
      result.append((char)nextChar);
      nextChar = in.read();
    }
    if (nextChar == -1) {
      atEof = true;
      in.unread(nextChar);
    }
    else if (nextChar == '\r') {
      nextChar = in.read();// check for cr/newline
      if (nextChar != '\n')
        in.unread(nextChar);
    }
    if (atEof && result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Read the next white space delimited string.
   * This will then be parsed by the appropriate
   * routine to return one of the desired types.
   */
  private void readToken() throws IOException {
    if (atEof) 
      throw new EOFException(
        "Attempt to read beyond the end of the stream.");
    if (!tokenRead) {
      //discard any buffered white space
      whiteSpaceBuffered = false; 
      StringBuffer result = new StringBuffer(80);
      int nextChar = input.read();
      while (Character.isWhitespace((char)nextChar))
        nextChar = input.read();
      while (nextChar != -1 && nextChar != '\n' && 
            nextChar != '\r' &&
            !Character.isWhitespace((char)nextChar)) 
      {
        result.append((char)nextChar);
        nextChar = input.read();
      }
      token = result.toString();
      if (nextChar == -1)
	if(token.length() == 0)
	  throw new EOFException(
	    "Attempt to read beyond the end of the stream.");
	else
	  atEof = true;
      input.unread(nextChar);
      tokenRead = true;
    }
  }

  private String token;
  private boolean tokenRead = false;
  private PushbackReader input;
  private boolean atEof = false;
  private boolean whiteSpaceBuffered = false;
  private char[] buffer = new char[1024];
  private int bufferCount;
}
