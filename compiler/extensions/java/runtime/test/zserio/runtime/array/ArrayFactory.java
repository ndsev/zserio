package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;

/**
 * An interface for factories of numeric arrays.
 *
 * This exists to make it possible to create and access the numeric runtime arrays in a uniform way.
 */
public interface ArrayFactory
{
    public ArrayWrapper create(int size);
    public ArrayWrapper create(long[] data, int offset, int length);
    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException;
}
