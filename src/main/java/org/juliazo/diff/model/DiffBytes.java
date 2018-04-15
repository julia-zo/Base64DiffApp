package org.juliazo.diff.model;

/**
 * POJO class for the actual Diff, expressed in bytes
 */
public class DiffBytes {

    /**
     * Location of the different byte
     */
    private int offset;

    /**
     * How many bytes are different starting from offset
     */
    private int length;

    /**
     * Gets offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets offset.
     *
     * @param offset the offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets length.
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets length.
     *
     * @param length the length
     */
    public void setLength(int length) {
        this.length = length;
    }
}
