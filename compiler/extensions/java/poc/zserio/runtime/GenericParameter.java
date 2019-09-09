package zserio.runtime;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public interface GenericParameter<E>
{
    public E get();

    public void set(E parameter);

    public int bitSizeOf(long bitPosition);

    public void read(BitStreamReader in) throws IOException, ZserioError;

    public long initializeOffsets(long bitPosition);

    public void write(BitStreamWriter out) throws IOException, ZserioError;
}
