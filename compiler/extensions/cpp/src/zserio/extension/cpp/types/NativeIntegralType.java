package zserio.extension.cpp.types;

import java.math.BigInteger;

import zserio.extension.common.ZserioExtensionException;

/**
 * Native C++ integral type mapping.
 */
public final class NativeIntegralType extends NativeBuiltinType
{
    public NativeIntegralType(int numBits, boolean isSigned, NativeArrayTraits arrayTraits)
    {
        super(getTypeName(numBits, isSigned), arrayTraits);
        addSystemIncludeFile(STDINT_INCLUDE);

        this.numBits = numBits;
        this.isSigned = isSigned;
        if (isSigned)
        {
            lowerBound = BigInteger.ONE.shiftLeft(numBits - 1).negate();
            upperBound = BigInteger.ONE.shiftLeft(numBits - 1).subtract(BigInteger.ONE);
        }
        else
        {
            lowerBound = BigInteger.ZERO;
            upperBound = BigInteger.ONE.shiftLeft(numBits).subtract(BigInteger.ONE);
        }
    }

    public BigInteger getLowerBound()
    {
        return lowerBound;
    }

    public BigInteger getUpperBound()
    {
        return upperBound;
    }

    public boolean isSigned()
    {
        return isSigned;
    }

    public int getNumBits()
    {
        return numBits;
    }

    public String formatLiteral(BigInteger value) throws ZserioExtensionException
    {
        checkRange(value);

        // TODO Not to compare strings. The same implementation is in formatting policy.
        final String stringValue = value.toString();

        // Special work around for INT64_MIN: this literal can't be written as a single number without a warning
        if (stringValue.equals(INT64_MIN))
            return "INT64_MIN";

        // ... and another special case: on 32 bit machines INT32_C(-2147483648) fails
        // (it evaluates to 2147483648, at least with gcc 4.4.3)
        if (stringValue.equals(INT32_MIN) && numBits <= 32)
            return "INT32_MIN";

        // use stdint.h's (U)INTn_C() macro
        final StringBuilder sb = new StringBuilder();

        if (!isSigned)
            sb.append('U');
        sb.append("INT");
        sb.append(numBits);
        sb.append("_C(");
        sb.append(stringValue);
        sb.append(')');
        return sb.toString();
    }

    private static String getTypeName(int numBits, boolean isSigned)
    {
        StringBuilder buffer = new StringBuilder();

        if (!isSigned)
            buffer.append('u');
        buffer.append("int");
        buffer.append(numBits);
        buffer.append("_t");

        return buffer.toString();
    }

    private void checkRange(BigInteger value) throws ZserioExtensionException
    {
        final BigInteger lowerBound = getLowerBound();
        final BigInteger upperBound = getUpperBound();
        if ((value.compareTo(getLowerBound()) < 0) || (value.compareTo(getUpperBound()) > 0))
            throw new ZserioExtensionException(
                    "Literal " + value + " out of range for native type: " + lowerBound + ".." + upperBound);
    }

    private final int numBits;
    private final boolean isSigned;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;

    private final static String STDINT_INCLUDE = "zserio/Types.h";
    private final static String INT64_MIN = "-9223372036854775808";
    private final static String INT32_MIN = "-2147483648";
}
