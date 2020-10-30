package zserio.ast;

/**
 * The interface for all Zserio types which have fixed bit size known during compilation.
 */
public interface FixedSizeType
{
    /**
     * Gets size of the type in bits.
     *
     * @return Returns bit size of the type.
     */
    public int getBitSize();
}
