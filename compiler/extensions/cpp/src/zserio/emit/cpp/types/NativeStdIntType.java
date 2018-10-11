package zserio.emit.cpp.types;

import java.math.BigInteger;
import java.util.ArrayList;

import zserio.emit.common.ZserioEmitException;

public class NativeStdIntType extends NativeIntegralType
{
    public NativeStdIntType(int nBits, boolean signed)
    {
        super(new ArrayList<String>(), formatName(nBits, signed), STDINT_INCLUDE);
        this.nBits = nBits;
        this.signed = signed;

        if (signed)
        {
            lowerBound = BigInteger.ONE.shiftLeft(nBits - 1).negate();
            upperBound = BigInteger.ONE.shiftLeft(nBits - 1).subtract(BigInteger.ONE);
        }
        else
        {
            lowerBound = BigInteger.ZERO;
            upperBound = BigInteger.ONE.shiftLeft(nBits).subtract(BigInteger.ONE);
        }
    }

    @Override
    public BigInteger getLowerBound()
    {
        return lowerBound;
    }

    @Override
    public BigInteger getUpperBound()
    {
        return upperBound;
    }

    @Override
    public boolean isSigned()
    {
        return signed;
    }

    @Override
    public int getBitCount()
    {
        return nBits;
    }

    @Override
    protected String formatLiteral(String rawValue) throws ZserioEmitException
    {
        // Special work around for INT64_MIN: this literal can't be written as a single number without a warning
        if (rawValue.equals(INT64_MIN))
            return "INT64_MIN";

        // ... and another special case: on 32 bit machines INT32_C(-2147483648) fails
        // (it evaluates to 2147483648, at least with gcc 4.4.3)
        if (rawValue.equals(INT32_MIN) && nBits <= 32)
            return "INT32_MIN";

        // use stdint.h's (U)INTn_C() macro
        final StringBuilder sb = new StringBuilder();

        if (!isSigned())
            sb.append('U');
        sb.append("INT");
        sb.append(nBits);
        sb.append("_C(");
        sb.append(rawValue);
        sb.append(')');
        return sb.toString();
    }

    /**
     * Create a name for a given standard integral type.
     * @param nBits  Number of bits. Must be one of 8, 16, 32, 64.
     * @param signed Signed or unsigned type.
     * @return       C++ name of the type, e.g. "uint8_t".
     */
    private final static String formatName(int nBits, boolean signed)
    {
        StringBuilder buffer = new StringBuilder();

        if (!signed)
            buffer.append('u');
        buffer.append("int");
        buffer.append(nBits);
        buffer.append("_t");

        return buffer.toString();
    }

    private final int nBits;
    private final boolean signed;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;

    private final static String STDINT_INCLUDE = "zserio/Types.h";
    private final static String INT64_MIN = "-9223372036854775808";
    private final static String INT32_MIN = "-2147483648";
}
