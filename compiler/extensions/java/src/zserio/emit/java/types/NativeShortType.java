package zserio.emit.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;

public class NativeShortType extends NativeIntegralType
{
    public NativeShortType(boolean nullable)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Short" : "short");
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
        return "(short)" + rawValue;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;

    private final BigInteger lowerBound = BigInteger.valueOf(Short.MIN_VALUE);
    private final BigInteger upperBound = BigInteger.valueOf(Short.MAX_VALUE);
}
