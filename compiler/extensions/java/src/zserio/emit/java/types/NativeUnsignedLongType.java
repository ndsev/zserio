package zserio.emit.java.types;

import java.math.BigInteger;
import zserio.ast.PackageName;

/**
 * Class used to simulate uint64 in Java.
 *
 * Long is not enough to fit, so this class uses BigInteger.
 *
 * The underlying type (BigInteger) is potentially unlimited.
 * As this class is to be used for storing Zserio values of type
 * uint64, the class claims the upper bound to be 2^64-1 as is true
 * for uint64_t.
 */
public class NativeUnsignedLongType extends NativeIntegralType
{
    public NativeUnsignedLongType()
    {
        super(BIG_INTEGER_PACKAGE, "BigInteger");
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
        return true;
    }

    @Override
    protected String formatLiteral(String rawValue)
    {
        return "new " + getFullName() + "(\"" + rawValue + "\")";
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    // emulate uint64_t
    private static final BigInteger lowerBound = BigInteger.ZERO;
    private static final BigInteger upperBound = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private static final PackageName BIG_INTEGER_PACKAGE =
            new PackageName.Builder().addId("java").addId("math").get();
}
