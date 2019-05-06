package zserio.ast;

/**
 * The base interface for all Zserio types used during emitting.
 */
public interface ZserioType extends AstNode
{
    /**
     * Gets the package in which this type is defined.
     *
     * @return The package in which this type is defined.
     */
    Package getPackage();

    /**
     * Gets the name of this type.
     *
     * @return Name of this type.
     */
    String getName();
}
