package zserio.emit.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;

public class NativeByteType extends NativeIntegralType
{
    public NativeByteType(boolean nullable)
    {
        super(PackageName.EMPTY, nullable ? "Byte" : "byte");
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
