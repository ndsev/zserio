package zserio.emit.cpp.types;

import java.math.BigInteger;
import java.util.List;
import zserio.emit.cpp.ZserioEmitCppException;

public abstract class NativeIntegralType extends CppNativeType
{
    public NativeIntegralType(List<String> namespacePath, String name, String includeFileName)
    {
        super(namespacePath, name, true);
        if (includeFileName != null)
            addSystemIncludeFile(includeFileName);
    }

    public NativeIntegralType(List<String> namespacePath, String name)
    {
        this(namespacePath, name, null);
    }

    /**
     * A simplified version of formatLiteral() that accepts a BigInteger value.
     *
     * @param value Value to format.
     * @return String representing a native literal for the value.
     */
    public String formatLiteral(BigInteger value) throws ZserioEmitCppException
    {
        checkRange(value);
        return formatLiteral(value.toString());
    }

    public abstract BigInteger getLowerBound();
    public abstract BigInteger getUpperBound();
    public abstract boolean isSigned();
    public abstract int getBitCount();

    /**
     * Format a literal for native type using already converted raw number.
     *
     * The string can be a C++ hexadecimal- or octal-style string, e.g.:
     * "0x123" or "0123".
     *
     * @param rawValue The formatted number.
     * @return Valid Java literal for the value.
     * @throws ZserioEmitCppException
     */
    protected abstract String formatLiteral(String rawValue) throws ZserioEmitCppException;

    private void checkRange(BigInteger value) throws ZserioEmitCppException
    {
        final BigInteger lowerBound = getLowerBound();
        final BigInteger upperBound = getUpperBound();
        if ((value.compareTo(getLowerBound()) < 0) || (value.compareTo(getUpperBound()) > 0))
            throw new ZserioEmitCppException("Literal " + value + " out of range for native type: " +
                    lowerBound + ".." + upperBound);
    }
}
