package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.SizeOf;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.InitializeOffsetsWriter;

/**
 * A dummy implementation of the Writer and SizeOf interface.
 */
public class WriterImplementer implements InitializeOffsetsWriter, SizeOf
{
    /**
     * Constructs a WriterImplementer object.
     *
     * @param id an identifier
     */
    public WriterImplementer(final int id)
    {
        this.id = id;
    }

    @Override
    public void write(final BitStreamWriter out) throws IOException, ZserioError
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws IOException, ZserioError
    {
        // TODO Auto-generated method stub
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final WriterImplementer other = (WriterImplementer)obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
        return Byte.SIZE;
    }

    @Override
    public long initializeOffsets(long bitPosition) throws ZserioError
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    /**
     * A write identifier.
     */
    private final int id;
}
