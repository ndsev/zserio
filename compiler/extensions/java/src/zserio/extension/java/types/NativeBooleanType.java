package zserio.extension.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;
import zserio.extension.common.ZserioExtensionException;

/**
 * Native Java boolean type mapping.
 */
public class NativeBooleanType extends NativeIntegralType
{
    public NativeBooleanType(boolean nullable)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Boolean" : "boolean",
                new NativeRawArray("BooleanRawArray"), new NativeArrayTraits("BoolArrayTraits"),
                new NativeArrayElement("BooleanArrayElement"));

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
    protected String formatLiteral(String rawValue) throws ZserioExtensionException
    {
        return formatLiteral(!rawValue.equals("0"));
    }

    private final boolean nullable;
}
