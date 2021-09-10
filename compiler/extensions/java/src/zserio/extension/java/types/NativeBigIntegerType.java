package zserio.extension.java.types;

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
public class NativeBigIntegerType extends NativeIntegralType
{
    public NativeBigIntegerType(NativeArrayTraits arrayTraits)
    {
        super(BIG_INTEGER_PACKAGE, "BigInteger",
                new NativeRawArray("BigIntegerRawArray"), arrayTraits,
                new NativeArrayElement("BigIntegerArrayElement"));
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

    // Bounds are lower than real BigInteger bounds (-2^Integer.MAX_VALUE, 2^Integer.MAX_VALUE)
    // to prevent big memory consumption of huge numbers stored in BigInteger. For our needs it's enough that
    // the bounds are higher than bounds of all Zserio integral types.
    private static final BigInteger lowerBound = BigInteger.ONE.shiftLeft(64).negate();
    private static final BigInteger upperBound = BigInteger.ONE.shiftLeft(64);
    private static final PackageName BIG_INTEGER_PACKAGE =
            new PackageName.Builder().addId("java").addId("math").get();
}
