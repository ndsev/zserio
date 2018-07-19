package zserio.ast;

/**
 * AST abstract node for all fixed size integer types.
 *
 * This is an abstract class for all built-in Zserio fixed size integer types (int8, uint8, bit:1,
 * int:1, ...).
 */
public abstract class FixedSizeIntegerType extends IntegerType
{
    private static final long serialVersionUID = 1L;
}
