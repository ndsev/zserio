package zserio.runtime.array;

/**
 * Interface used to set array indexed offsets during offset initialization.
 */
public interface OffsetInitializer
{
    /**
     * Sets the bit stream offset to indexed offset.
     *
     * @param index      Index of element in array.
     * @param byteOffset Current bit stream offset in bytes.
     */
    void setOffset(int index, long byteOffset);
}
