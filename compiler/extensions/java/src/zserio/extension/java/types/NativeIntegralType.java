package zserio.extension.java.types;

import java.math.BigInteger;

import zserio.ast.PackageName;
import zserio.extension.common.ZserioExtensionException;

/**
 * Native Java integral type mapping.
 */
public abstract class NativeIntegralType extends NativeArrayableType
{
    public NativeIntegralType(PackageName packageName, String name,
            NativeRawArray rawArray, NativeArrayTraits arrayTraits, NativeArrayElement arrayElement)
    {
        super(packageName, name, rawArray, arrayTraits, arrayElement);
    }

    public abstract boolean requiresBigInt();

    public abstract BigInteger getLowerBound();
    public abstract BigInteger getUpperBound();

    // simplified version of formatLiteral() that accepts a BigInteger value.
    public String formatLiteral(BigInteger value) throws ZserioExtensionException
    {
        checkRange(value);
        return formatLiteral(value.toString());
    }


    /*
     * Format a literal for native type using already converted raw number.
     *
     * The string can be a Java hexadecimal- or octal-style string, e.g.:
     * "0x123" or "0123".
     *
     * Currently binary literals ("0b101") can't be used as they are present only in Java 1.7+.
     */
    protected abstract String formatLiteral(String rawValue) throws ZserioExtensionException;

    private void checkRange(BigInteger value) throws ZserioExtensionException
    {
        final BigInteger lowerBound = getLowerBound();
        final BigInteger upperBound = getUpperBound();
        if ((value.compareTo(getLowerBound()) < 0) || (value.compareTo(getUpperBound()) > 0))
            throw new ZserioExtensionException("Literal " + value + " out of range for native type: " +
                    lowerBound + ".." + upperBound);
    }
}
