package zserio.runtime.array;

import zserio.runtime.ZserioError;

/**
 * Interface used to check array indexed offsets during reading/writing from/to bit stream.
 */
public interface OffsetChecker
{
    /**
     * Checks the bit stream offset for indexed offsets.
     *
     * @param index      Index of element in array.
     * @param byteOffset Current bit stream offset in bytes.
     *
     * @throws ZserioError Throws if offset offset is not correct.
     */
    void checkOffset(int index, long byteOffset) throws ZserioError;
}
