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
     */
    public static int convertBitBufferSizeToInt(long value)
    {
        if (value > Integer.MAX_VALUE)
            throw new ZserioError("VarSizeUtil: Value '" + value + "' is out of bounds for conversion!");

        return (int)value;
    }
}
