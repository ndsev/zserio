package zserio.runtime;

/**
 * Provide converting functions from varsize values which check range correctness.
 */
public final class VarSizeUtil
{
    /**
     * Converts bit buffer size to signed integer value.
     *
     * @param value Bit buffer size to convert.
     *
     * @return Checked signed integer value.
     *
     * @throws ZserioError Throws if bit buffer size cannot be converted to signed integer value.
     */
    public static int convertBitBufferSizeToInt(long value) throws ZserioError
    {
        if (value > Integer.MAX_VALUE)
            throw new ZserioError("VarSizeUtil: Value '" + value + "' is out of bounds for conversion!");

        return (int) value;
    }
}
