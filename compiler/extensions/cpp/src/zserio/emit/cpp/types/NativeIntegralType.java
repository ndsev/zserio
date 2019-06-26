package zserio.emit.cpp.types;

import java.math.BigInteger;
import zserio.ast.PackageName;
import zserio.emit.common.ZserioEmitException;

public class NativeIntegralType extends CppNativeType
{
    public NativeIntegralType(int numBits, boolean isSigned)
    {
        super(PackageName.EMPTY, getCppTypeName(numBits, isSigned));
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

    public String formatLiteral(BigInteger value) throws ZserioEmitException
    {
        checkRange(value);

        // TODO not to compare strings
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

    private static String getCppTypeName(int numBits, boolean isSigned)
    {
        StringBuilder buffer = new StringBuilder();

        if (!isSigned)
            buffer.append('u');
        buffer.append("int");
        buffer.append(numBits);
        buffer.append("_t");

        return buffer.toString();
    }

    private void checkRange(BigInteger value) throws ZserioEmitException
    {
        final BigInteger lowerBound = getLowerBound();
        final BigInteger upperBound = getUpperBound();
        if ((value.compareTo(getLowerBound()) < 0) || (value.compareTo(getUpperBound()) > 0))
            throw new ZserioEmitException("Literal " + value + " out of range for native type: " +
                    lowerBound + ".." + upperBound);
    }

    private final int numBits;
    private final boolean isSigned;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;

    private final static String STDINT_INCLUDE = "zserio/Types.h";
    private final static String INT64_MIN = "-9223372036854775808";
    private final static String INT32_MIN = "-2147483648";
}
