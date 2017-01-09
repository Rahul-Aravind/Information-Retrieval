// CSE 143 Homework 7: Huffman Coding  (instructor-provided file)

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The BitOutputStream and BitInputStream classes provide the ability to
 * write and read individual bits to a file in a compact form.  One minor
 * limitation of this approach is that the resulting file will always have
 * a number of bits that is a multiple of 8.  In effect, whatever bits are
 * output to the file are padded at the end with 0s to make the total
 * number of bits a multiple of 8.
 * 
 * @author Marty Stepp, Stuart Reges, Helene Martin, and Owen Astrachan
 * @version 2012-03-02
 */
public class BitOutputStream extends PrintStream {
    public static final boolean DEBUG = false;   // set true for debug printlns
    public static final int BYTE_SIZE = 8;       // digits per byte
    public static final char EOF = (char) 256;   // character for end-of-file
    
    /**
     * Converts the given character into a format that prints well on the screen.
     * For most characters, this just means enclosing it in ' ' marks.
     * For special characters such as \n, replaces them with a \ and an n.
     * Also replaces the special EOF character with the string "EOF".
	 * Most characters with low ASCII values (< 32) are printed as a decimal number.
     * @param ch the character to convert, such as '\n'
     * @return the formatted string, such as "'\\n'"
     */
    public static String toPrintable(char ch) {
    	switch (ch) {
            case '\0':                 return "'\\0'";
            case '\n':                 return "'\\n'";
            case '\r':                 return "'\\r'";
            case '\t':                 return "'\\t'";
            case '\f':                 return "'\\f'";
            case BitOutputStream.EOF:  return "EOF";
            default:                   
				if (ch < 32) {
					return String.format("%03d", (int) ch);  // control characters in binary files
				} else {
					return "'" + ch + "'";
				}
        }
    }


    private OutputStream output;  // actual target to write to
    private boolean open;         // true if still open for writing
    private int digits;           // buffer to build up next byte's digits <= 8
    private int numDigits;        // how many digits are currently in buffer
    private boolean bitMode;      // true if writing bits; false to debug ASCII
    private boolean seenEOF;      // true if this output stream has written EOF
    private String eofEncoding;   // bits to write at end of file to mark EOF
    
    /**
     * Creates a BitOutputStream to send output to the stream in 'bit mode'.
     * Precondition: The given file is legal and can be written.
     * @param output the target output stream to write.
     * @throws BitIOException if the file cannot be opened for writing. 
     */
    public BitOutputStream(OutputStream output) {
        this(output, true);
    }
    
    /**
     * Creates a BitOutputStream to send output to the stream in the given mode.
     * Precondition: The given file is legal and can be written.
     * @param output the target output stream to write.
     * @param bitMode true to write bits at a time; false to write ASCII 
     *                characters (bytes) at a time for debugging.
     * @throws BitIOException if the file cannot be opened for writing. 
     */
    public BitOutputStream(OutputStream output, boolean bitMode) {
    	super(output);
        this.output = output;
        setBitMode(bitMode);
        digits = 0;
        numDigits = 0;
        open = true;
        seenEOF = false;
    }

    /**
     * Closes this output stream for writing and flushes any data to be written.
     */
    public void close() {
        if (open) {
            // add pseudo-EOF character (so BitInputStream knows where EOF is)
            if (eofEncoding != null) {
            	writeBits(eofEncoding);
            }
            
            if (numDigits > 0) {
                flush();

                if (!bitMode) {
                    // pad to a multiple of 8 bits to match bit mode
                    for (int i = numDigits; i < BYTE_SIZE; i++) {
						writeBit(0);
                    }
                }
            }
            
            try {
				output.close();
			} catch (IOException e) {
				throw new BitIOException(e);
			}
            open = false;
            seenEOF = true;
        }
    }

    /**
     * Flushes the buffer.  If numDigits < BYTE_SIZE, this will 
     * effectively pad the output with extra 0s, so this should
     * be called only when numDigits == BYTE_SIZE or when we are
     * closing the output.
     */
    public void flush() {
        if (inBitMode()) {
        	write(digits);
            digits = 0;
            numDigits = 0;
        }
    }
    
    /**
     * Returns true if this output stream has ever written an EOF character;
     * students should not call this method.
     * This is a hack so that a BitInputStream knows when it is out of bits.
     * @return true if EOF has been seen, otherwise false.
     */
    public boolean hasSeenEOF() {
        return seenEOF;
    }
    
    /**
     * Returns whether this stream is in real 'bit mode', writing a bit to the
     * file for each call to writeBit.  The alternative is 'byte mode', where a 
     * full byte (ASCII character) of '0' or '1' is written for each call to 
     * writeBit, to make it easier to debug your program.
     * @return true if in bit mode, false if in byte mode.
     */
    public boolean inBitMode() {
        return bitMode;
    }
    
    /**
     * Sets whether this stream is in real 'bit mode', writing a bit to the
     * file for each 0 or 1 written in writeBit (as described under inBitMode).
     * Ignores the caller and always uses 'byte' mode if writing to System.out
     * or if the JVM bitstream.bitmode environment variable is set.
     * @param bitMode true to use bit mode, false to use character mode.
     */
    public void setBitMode(boolean bitMode) {
        this.bitMode = bitMode && output != System.out && output != System.err
                && System.getProperty("bitstream.bitmode") == null;
    }
    
    /**
     * Sets this bit output stream to use the given pattern of bits as its
     * encoding for the end of a file (EOF); students should not call this method.
     * @param eofEncoding the EOF encoding to use
     */
    public void setEOFEncoding(String eofEncoding) {
        this.eofEncoding = eofEncoding;
    }

    /**
     * Writes the given byte to output.
     * @param b the byte value to write.
     * @throws BitIOException if unable to write to the underlying output stream
     */
    public void write(int b) {
        if (b == EOF || seenEOF) {
            if (DEBUG) System.out.println("  ** BitOutputStream EOF seen");
            seenEOF = true;
        } else {
            if (DEBUG) System.out.println("  ** BitOutputStream write: " + b + " (" 
                    + toPrintable((char) b) + ")");
            try {
				output.write(b);
			} catch (IOException e) {
				throw new BitIOException(e);
			}
        }
    }
    
    /**
     * Prints the given character to this output stream.
     * @param c The character to print.
     */
    public void print(char c) {
    	write(c);
    }
    
    /**
     * Prints the given Object to this output stream.
     * @param c The Object to print.
     */
    public void print(Object o) {
    	if (o instanceof Character) {
    		write((Character) o);
    	} else {
    		super.print(o);
    	}
    }
    
    /**
     * Writes given bit to output.
     * @param bit the bit value to write.
     * @throws IllegalArgumentException if bit is not 0 or 1
     */
    public void writeBit(int bit) {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Illegal bit value: " + bit);
        }
        
        if (inBitMode()) {
            // pad shifted bit into our digit buffer; flush when we hit 8 digits
            digits += bit << numDigits;
            numDigits++;
            if (DEBUG) System.out.println("  ** BitOutputStream writeBit: " + bit);
            if (numDigits == BYTE_SIZE) {
                flush();
            }
        } else {
            // non-bit mode; just write ASCII character
            numDigits = (numDigits + 1) % 8;
            write((char) (bit + '0'));
        }
    }
    
    /**
     * Writes every bit in the given string of 0s and 1s.
     * @param bits A string entirely of 0s and 1s, such as "01001100110".
     * @throws IllegalArgumentException if the string contains any characters
     *                                  other than '0' or '1'.
     */
    public void writeBits(String bits) {
        for (int i = 0; i < bits.length(); i++) {
            char ch = bits.charAt(i);
            if (ch == '0' || ch == 0) {
                writeBit(0);
            } else if (ch == '1' || ch == 1) {
                writeBit(1);
            } else {
                throw new IllegalArgumentException("Illegal bit value '" + ch + 
                        "' at index " + i + " of string \"" + bits + "\"");
            }
        }
    }

    /**
     * Writes every character in the given string of 0s and 1s as a byte.
     * @param bytes A string entirely of 0s and 1s, such as "01001100110".
     * @throws IllegalArgumentException if the string contains any characters
     *                                  other than '0' or '1'.
     */
    public void writeBytes(String bytes) {
        for (int i = 0; i < bytes.length(); i++) {
            char ch = bytes.charAt(i);
            if (ch == '0' || ch == 0) {
                write('0');
            } else if (ch == '1' || ch == 1) {
                write('1');
            } else {
                throw new IllegalArgumentException("Illegal bit value '" + ch + 
                        "' at index " + i + " of string \"" + bytes + "\"");
            }
        }
    }

    /**
     * Runs when the object is garbage collected / program shuts down.
     * Included to ensure that the stream is closed.
     */
    protected void finalize() {
        close();
    }
    
    /**
     * A class to represent bit I/O errors as runtime exceptions.
     * Used to avoid the forced checked exceptions usually thrown in Java I/O.
     */
    public static class BitIOException extends RuntimeException {
    	private static final long serialVersionUID = 0L;
    	
    	
    	public BitIOException(String message) {
    		super(message);
    	}
    	
    	public BitIOException(Throwable cause) {
    		super(cause);
    	}
    	
    	public BitIOException(String message, Throwable cause) {
    		super(message, cause);
    	}
    }
}