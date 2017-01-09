// CSE 143 Homework 7: Huffman Coding  (instructor-provided file)

import java.io.InputStream;
import java.io.IOException;

/**
 * The BitOutputStream and BitInputStream classes provide the ability to
 * write and read individual bits to a file in a compact form.  One minor
 * limitation of this approach is that the resulting file will always have
 * a number of bits that is a multiple of 8.  In effect, whatever bits are
 * output to the file are padded at the end with 0s to make the total
 * number of bits a multiple of 8.
 * 
 * @author Marty Stepp, Stuart Reges, Helene Martin, and Owen Astrachan
 * @version 2012-03-01
 */
public class BitInputStream extends InputStream {
    private InputStream input;    // actual source to read from
    private int digits;           // buffer to build up next byte's digits <= 8
    private int numDigits;        // how many digits are currently in buffer
    private boolean bitMode;      // true if writing bits; false to debug ASCII
    private BitOutputStream partner;  // another stream to monitor for EOF

    /**
     * Creates a BitInputStream reading bits of input from the given stream source.
     * @param input the input stream to read.
     * @throws BitIOException if the input stream cannot be accessed.
     */
    public BitInputStream(InputStream input) {
        this(input, true);
    }
    
    /** 
     * Creates a BitInputStream reading bits/bytes input from the given stream source.
     * @param input the input stream to read.
     * @param bitMode true to write bits at a time; false to write ASCII 
     *                characters (bytes) at a time for debugging.
     * @throws BitIOException if the input stream cannot be accessed.
     */
    public BitInputStream(InputStream input, boolean bitMode) {
        this(input, null, bitMode);
    }
    
    /** 
     * Creates a BitInputStream reading bits/bytes input from the given stream 
     * source and partnered with the given bit output stream.
     * @param input the input stream to read.
     * @param partner a "partner" bit output stream to watch for EOF.
     * @param bitMode true to write bits at a time; false to write ASCII 
     *                characters (bytes) at a time for debugging.
     * @throws BitIOException if the input stream cannot be accessed.
     * @throws NullPointerException if the given input or output stream is null.
     * (The partner can be null; simply won't partner with any bit output stream
     * in such a case.)
     */
    public BitInputStream(InputStream input, BitOutputStream partner, boolean bitMode) {
        if (input == null) {
            throw new NullPointerException("should not pass a null input stream");
        }
        this.input = input;
        setBitMode(bitMode);
        setPartner(partner);
        digits = 0;
        numDigits = 0;
        read();    // initialize buffer
    }
    
    /**
     * Returns an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking by the next invocation of a
     * method for this input stream.
     * @return an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking, or 0 when it reaches the
     * end of the input stream.
     * @throws BitIOException if the input stream cannot be accessed.
     */
    @Override
    public int available() {
        try {
        	return input.available();
        } catch (IOException ioe) {
        	throw new BitOutputStream.BitIOException(ioe);
        }
    }

    /**
     * Closes this stream for reading. 
     * @throws BitIOException if the input stream cannot be closed.
     */
    @Override
    public void close() {
        try {
        	input.close();
        } catch (IOException ioe) {
        	throw new BitOutputStream.BitIOException(ioe);
        }
    }

    /**
     * Returns whether this stream has more bits available to be read.
     * @return true if more bits are available, otherwise false.
     */
    public boolean hasNextBit() {
        if (partner != null) {
            return !partner.hasSeenEOF() && digits != -1;
        } else {
            return digits != -1;
        }
    }
    
    /**
     * Returns whether this stream is in real 'bit mode', reading a bit from the
     * file for each call to readBit.  The alternative is 'byte mode', 
     * where a full byte (character) is read from the file each time readBit is 
     * called, to make it easier to debug your program.
     * @return true if in bit mode, false if in byte mode.
     */
    public boolean inBitMode() {
        return bitMode;
    }
    
    /**
     * Reads and returns a single byte of information from this stream.
     * @return the byte of information read, or -1 if no bytes remain to read.
     * @throws BitIOException if the input stream cannot be read.
     */
    @Override
    public int read() {
        int result = readByte();
        if (BitOutputStream.DEBUG) System.out.println("  ** BitInputStream read: " + result + 
                " (" + BitOutputStream.toPrintable((char) result) + ")");
        return result;
    }

    /**
     * Reads and returns some bytes of information from this stream into
     * the given array.
     * @param bytes array of byte to fill
     * @return the number of bytes read, or -1 if no bytes remain to read.
     * @throws BitIOException if the input stream cannot be read.
     * @throws NullPointerException if bytes is null. 
     */
    @Override
    public int read(byte[] bytes) {
        return read(bytes, 0, bytes.length);
    }

    /**
     * Reads and returns some bytes of information from this stream into
     * the given array.
     * @param bytes array of byte to fill
     * @return the number of bytes read, or -1 if no bytes remain to read.
     * @throws BitIOException if the input stream cannot be read.
     * @throws NullPointerException if bytes is null. 
     * @throws ArrayIndexOutOfBoundsException if offset + length is past the end
     *                                        of the array. 
     */
    @Override
    public int read(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            throw new NullPointerException("should not pass a null byte array");
        }
        int count = 0;
        while (count < length) {
            int b = readByte();
            if (b < 0) {
                if (count == 0) {
                    return -1;
                } else {
                    break;
                }
            }
            bytes[offset + count] = (byte) b;
        }
        return count;
    }

    /**
     * Reads and returns the next single bit of input from this stream.
     * @return the bit of information read, or -1 if no bits remain to read.
     * @throws BitIOException if the input stream cannot be read.
     */
    public int readBit() {
        int result = -1;
        if (hasNextBit()) {
            if (inBitMode()) {
                // read a single bit from our 1-byte buffer
                result = digits % 2;
                if (BitOutputStream.DEBUG) System.out.println("  ** BitInputStream readBit: " + result);
                digits /= 2;
                numDigits++;
                if (numDigits == BitOutputStream.BYTE_SIZE) {
                    read();   // replenish buffer if empty
                }
            } else {
                // read an entire byte
                result = read();
                if (result != '0' && result != '1') {
                    throw new BitOutputStream.BitIOException("Illegal bit (byte) value: " + result + 
                            " (this is probably a binary-compressed file)");
                }
                result -= '0';
            }
        }
        return result;
    }

    /**
     * Reads and returns an entire line of text from this bit input stream as a String.
     * You would not normally want to call this method while you're reading bits
     * from a bit input stream; it is provided as a convenience for students doing
     * extra credit functionality in the assignment.
     * @return the line read;  an empty string "" if no input remains.
     * @throws BitIOException if the input stream cannot be read.
     */
    public String readLine() {
        StringBuilder line = new StringBuilder();
        while (true) {
            int n = read();
            if (n < 0 || n == '\n') {
                break;
            }
            if (n != '\r') {
            	// skip \r characters (Windows only) to improve platform-independence
                line.append((char) n);
            }
        }
        if (BitOutputStream.DEBUG) System.out.println("  ** BitInputStream readLine: \"" + line + "\"");
        return line.toString();
    }
    
    /**
     * Sets whether this stream is in real 'bit mode', reading a bit from the
     * file for each call to readBit (as described under inBitMode).
     * Ignores the caller and always uses 'byte' mode if reading from System.in
     * or if the JVM bitstream.bitmode environment variable is set.
     * @param bitMode true to use bit mode, false to use byte mode.
     */
    public void setBitMode(boolean bitMode) {
        this.bitMode = bitMode && input != System.in
                && System.getProperty("bitstream.bitmode") == null;
    }

    /**
     *  Called by Java when the program is shutting down;
     *  included to help ensure that the stream is closed.
     */
    protected void finalize() {
        close();
    }

    // Refreshes the internal buffer with the next BYTE_SIZE bits.
    private int readByte() {
        int result = digits;
        try {
        	digits = input.read();
        } catch (IOException ioe) {
        	throw new BitOutputStream.BitIOException(ioe);
        }
        numDigits = 0;
        return result;
    }
    
    // Sets this bit input stream's "partner" output stream; 
    // students should not call this method.
    // This is a hack so that the input stream can detect the outputting
    // of a pseudo-EOF character to a partnered output stream.
    private void setPartner(BitOutputStream partner) {
        this.partner = partner;
    }
}