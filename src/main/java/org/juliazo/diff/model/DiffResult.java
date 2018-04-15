package org.juliazo.diff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * POJO class for the result of the diff operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiffResult {

    /**
     * The Id. Provided by the user.
     */
    private String id;

    /**
     * Indicates whether the right and left have the same size
     */
    private boolean equalSize;

    /**
     * Indicates whether the right and left input data are equal
     * Primitive type boolean was not used here to allow this property to be
     * set as null. When property equalSize is false, equals will be null,
     * thus not being visible on the response payload.
     */
    private Boolean equals;

    /**
     * List of the differences found between right and left input data.
     * Expressed in {@link org.juliazo.diff.model.DiffBytes}
     * This list will only be shown in the response payload when there are differences in the input data.
     */
    private List<DiffBytes> differences;

    /**
     * Instantiates a new Diff result.
     */
    public DiffResult() {

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
     * Is equal size boolean.
     *
     * @return the boolean
     */
    public boolean isEqualSize() {
        return equalSize;
    }

    /**
     * Sets equal size.
     *
     * @param equalSize the equal size
     */
    public void setEqualSize(boolean equalSize) {
        this.equalSize = equalSize;
    }

    /**
     * Is equals Boolean.
     *
     * @return the boolean
     */
    public Boolean isEquals() {
        return equals;
    }

    /**
     * Sets equals.
     *
     * @param equals the equals
     */
    public void setEquals(Boolean equals) {
        this.equals = equals;
    }

    /**
     * Gets differences.
     *
     * @return the differences
     */
    public List<DiffBytes> getDifferences() {
        return differences;
    }

    /**
     * Sets differences.
     *
     * @param differences the differences
     */
    public void setDifferences(List<DiffBytes> differences) {
        this.differences = differences;
    }
}
