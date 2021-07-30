package zserio.extension.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;

public class NativeIntType extends NativeIntegralType
{
    public NativeIntType(boolean nullable, NativeArrayTraits arrayTraits)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Integer" : "int", arrayTraits);

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
        return rawValue;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;

    private static final BigInteger lowerBound = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger upperBound = BigInteger.valueOf(Integer.MAX_VALUE);
}
