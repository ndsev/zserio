package zserio.runtime.array;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;

/**
 * This class implements an zserio.runtime.Array for numeric arrays.
 *
 * The actual underlying array is not defined here because Java generics work only for non-primitive types.
 *
 * @param <E> A non-primitive version of the type of the element stored (for primitives, it's their
 *            non-primitive counterpart, e.g. Integer for int).
 */
abstract class NumericArrayBase<E> extends ArrayBase<E>
{
    /**
     * Returns length of array stored in bit stream in bits.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    protected int bitSizeOfImpl(long bitPosition, int numBits)
    {
        return numBits * length();
    }

    /**
     * Returns length of auto length array stored in bit stream in bits.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    protected int bitSizeOfAutoImpl(long bitPosition, int numBits)
    {
        return BitSizeOfCalculator.getBitSizeOfVarUInt64(length()) + bitSizeOfImpl(bitPosition, numBits);
    }

    /**
     * Returns length of aligned auto length array stored in bit stream in bits.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    protected int bitSizeOfAlignedAutoImpl(long bitPosition, int numBits)
    {
        return BitSizeOfCalculator.getBitSizeOfVarUInt64(length()) + bitSizeOfAlignedImpl(bitPosition, numBits);
    }

    /**
     * Returns length of aligned array stored in bit stream in bits.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    protected int bitSizeOfAlignedImpl(long bitPosition, int numBits)
    {
        if (length() > 1)
            return BitPositionUtil.alignTo(Byte.SIZE, numBits) * (length() - 1) + numBits;
        else
            return bitSizeOfImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the array.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    protected long initializeOffsetsImpl(long bitPosition, int numBits)
    {
        return bitPosition + bitSizeOfImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the auto length array.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    protected long initializeOffsetsAutoImpl(long bitPosition, int numBits)
    {
        return bitPosition + bitSizeOfAutoImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the aligned auto length array.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    protected long initializeOffsetsAlignedAutoImpl(long bitPosition, int numBits, OffsetSetter setter)
    {
        long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarUInt64(length());

        return initializeOffsetsAlignedImpl(currentBitPosition, numBits, setter);
    }

    /**
     * Initializes indexed offsets for the aligned length array.
     *
     * This is common implementation for all arrays with numeric elements.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    protected long initializeOffsetsAlignedImpl(long bitPosition, int numBits, OffsetSetter setter)
    {
        long currentBitPosition = bitPosition;
        for (int index = 0; index < length(); index++)
        {
            currentBitPosition = BitPositionUtil.alignTo(Byte.SIZE, currentBitPosition);
            setter.setOffset(index, BitPositionUtil.bitsToBytes(currentBitPosition));
            currentBitPosition += numBits;
        }

        return currentBitPosition;
    }
}
