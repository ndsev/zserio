package zserio.ast;


/**
 * The base interface for all Zserio types used during emitting.
 */
public interface ZserioType
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

    /**
     * Gets the list of Zserio types used by this type (e.g. used in compound field definition).
     *
     * This is not supported by all derived classes.
     *
     * @return List of Zserio types used by this type.
     */
    Iterable<ZserioType> getUsedTypeList();

    /**
     * Calls visitor to extend functionality of this interface.
     *
     * @param visitor Visitor to call.
     */
    void callVisitor(ZserioTypeVisitor visitor);
}
