package zserio.runtime.array;

/**
 * Interface used to check array indexed offsets during reading/writing of arrays from/to bit stream.
 */
public interface OffsetChecker
{
    /**
     * Checks the bit stream offset for indexed offsets.
     *
     * @param index      Index of element in array.
     * @param byteOffset Current bit stream offset in bytes.
     */
    void checkOffset(int index, long byteOffset);
}
