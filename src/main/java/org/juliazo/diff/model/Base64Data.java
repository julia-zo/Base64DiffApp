package org.juliazo.diff.model;

/**
 * POJO Class to hold Base64 encoded data
 */
public class Base64Data {

    /**
     * The Id. Each set of data has its own ID for security reasons
     */
    private String id;

    /**
     * The Left data.
     */
    private String leftData;

    /**
     * The Right data.
     */
    private String rightData;

    /**
     * Instantiates a new Base 64 data.
     *
     * @param id        the id
     * @param leftData  the left data
     * @param rightData the right data
     */
    public Base64Data(String id, String leftData, String rightData) {
        this.id = id;
        this.leftData = leftData;
        this.rightData = rightData;
    }

    /**
     * Empty constructor.
     */
    public Base64Data() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets left data.
     *
     * @return the left data
     */
    public String getLeftData() {
        return leftData;
    }

    /**
     * Sets left data.
     *
     * @param leftData the left data
     */
    public void setLeftData(String leftData) {
        this.leftData = leftData;
    }

    /**
     * Gets right data.
     *
     * @return the right data
     */
    public String getRightData() {
        return rightData;
    }

    /**
     * Sets right data.
     *
     * @param rightData the right data
     */
    public void setRightData(String rightData) {
        this.rightData = rightData;
    }
}
