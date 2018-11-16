package zserio.emit.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;
import zserio.emit.common.ZserioEmitException;

public class NativeBooleanType extends NativeIntegralType
{
    public NativeBooleanType(boolean nullable)
    {
        super(PackageName.EMPTY, nullable ? "Boolean" : "boolean");
        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    @Override
    public boolean requiresBigInt()
    {
        return false;
    }

    @Override
    public BigInteger getLowerBound()
    {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getUpperBound()
    {
        return BigInteger.ONE;
    }

    public String formatLiteral(boolean value)
    {
        return (value) ? "true" : "false";
    }

    @Override
    protected String formatLiteral(String rawValue) throws ZserioEmitException
    {
        return formatLiteral(!rawValue.equals("0"));
    }

    private final boolean nullable;
}
