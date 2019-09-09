package zserio.runtime;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.ZserioReader;
import zserio.runtime.ZserioWriter;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class ObjectGenericParameter<OBJECT extends ZserioReader & ZserioWriter> implements GenericParameter<OBJECT>
{
    @Override
    public OBJECT get()
    {
        return parameter;
    }

    @Override
    public void set(OBJECT parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
        return parameter.bitSizeOf(bitPosition);
    }

    @Override
    public void read(BitStreamReader in) throws IOException, ZserioError
    {
        parameter.read(in);
    }

    @Override
    public long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    @Override
    public void write(BitStreamWriter out) throws IOException, ZserioError
    {
        parameter.write(out, false);
    }

    private OBJECT parameter;
}
