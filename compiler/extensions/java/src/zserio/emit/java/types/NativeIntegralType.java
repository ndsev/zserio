package zserio.emit.java.types;

import java.math.BigInteger;

import zserio.emit.java.ZserioEmitJavaException;

abstract public class NativeIntegralType extends JavaNativeType
{
    public NativeIntegralType(String packageName, String name)
    {
        super(packageName, name);
    }

    /**
     * Check if the type is based on BigInt.
     *
     * @return True if arithmetics with the type requires BigInt.
     */
    public abstract boolean requiresBigInt();

    public abstract BigInteger getLowerBound();
    public abstract BigInteger getUpperBound();

    /**
     * A simplified version of formatLiteral() that accepts a BigInteger value.
     *
     * @param value Value to format.
     * @return String representing a native literal for the value.
     */
    public String formatLiteral(BigInteger value) throws ZserioEmitJavaException
    {
        checkRange(value);
        return formatLiteral(value.toString());
    }

    public boolean isSigned()
    {
        // all java integral types are signed
        return true;
    }

    /**
     * Format a literal for native type using already converted raw number.
     *
     * The string can be a Java hexadecimal- or octal-style string, e.g.:
     * "0x123" or "0123".
     *
     * Currently binary literals ("0b101") can't be used as they are present only in Java 1.7+.
     *
     * @param rawValue The formatted number.
     * @return Valid Java literal for the value.
     * @throws ZserioEmitJavaException
     */
    protected abstract String formatLiteral(String rawValue) throws ZserioEmitJavaException;

    private void checkRange(BigInteger value) throws ZserioEmitJavaException
    {

        final BigInteger lowerBound = getLowerBound();
        final BigInteger upperBound = getUpperBound();
        if ((value.compareTo(getLowerBound()) < 0) || (value.compareTo(getUpperBound()) > 0))
            throw new ZserioEmitJavaException("Literal " + value + " out of range for native type: " +
                    lowerBound + ".." + upperBound);
    }
}
