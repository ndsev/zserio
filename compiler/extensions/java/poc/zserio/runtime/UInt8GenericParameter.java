package zserio.runtime;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class UInt8GenericParameter implements GenericParameter<Short>
{
    @Override
    public Short get()
    {
        return parameter;
    }

    @Override
    public void set(Short parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
        return 8;
    }

    @Override
    public void read(BitStreamReader in) throws IOException, ZserioError
    {
        parameter = in.readUnsignedByte();
    }

    @Override
    public long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    @Override
    public void write(BitStreamWriter out) throws IOException, ZserioError
    {
        out.writeUnsignedByte(parameter);
    }

    private Short parameter;
}
