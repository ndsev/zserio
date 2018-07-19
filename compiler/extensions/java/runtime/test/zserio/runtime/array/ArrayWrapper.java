package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamWriter;

/**
 * A wrapper interface for uniform access to the runtime numeric arrays.
 */
public interface ArrayWrapper extends Iterable<Long>
{
    void setElementAt(long value, int index);
    long elementAt(int index);
    int length();
    int bitSizeOf(long bitPosition, int numBits);
    int sum();
    ArrayWrapper subRange(int offset, int length);
    int hashCode();
    public boolean equals(Object other);
    Iterator<Long> iterator();
    void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError;
}
