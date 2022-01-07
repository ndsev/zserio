package zserio.extension.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;

/**
 * Native Java byte type mapping.
 */
public class NativeByteType extends NativeIntegralType
{
    public NativeByteType(boolean nullable, NativeArrayTraits arrayTraits)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Byte" : "byte",
                new NativeRawArray("ByteRawArray"), arrayTraits, new NativeArrayElement("ByteArrayElement"));

        this.nullable = nullable;
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
    public boolean requiresBigInt()
    {
        return false;
    }

    @Override
    protected String formatLiteral(String rawValue)
    {
        return "(byte)" + rawValue;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;

    private static final BigInteger lowerBound = BigInteger.valueOf(Byte.MIN_VALUE);
    private static final BigInteger upperBound = BigInteger.valueOf(Byte.MAX_VALUE);
}
