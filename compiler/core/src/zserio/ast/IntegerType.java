package zserio.ast;

import java.math.BigInteger;

/**
 * AST abstract node for all Integer types.
 *
 * This is an abstract class for all built-in Zserio Integer types (int8, uint8, bit:1, int:1, varint16,
 * varuint16, ...).
 */
public abstract class IntegerType extends BuiltInType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public IntegerType(AstLocation location, String name)
    {
        super(location, name);
    }

    /**
     * Gets upper bound for this integer type.
     *
     * @return Upper bound or null if bit size of this integer type is unknown during compilation time.
     */
    public abstract BigInteger getUpperBound();

    /**
     * Gets lower bound for this integer type.
     *
     * @return Lower bound or null if bit size of this integer type is unknown during compilation time.
     */
    public abstract BigInteger getLowerBound();

    /**
     * Checks if this integer type is signed or not.
     *
     * @return true if this integer type is signed.
     */
    public abstract boolean isSigned();
}
