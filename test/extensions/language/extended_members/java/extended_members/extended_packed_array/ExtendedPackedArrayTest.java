package extended_members.extended_packed_array;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

import test_utils.CompoundUtil;

public class ExtendedPackedArrayTest
{
    public Extended1 createExtended1()
    {
        Extended1 data = new Extended1();

        Element[] arr = new Element[ARRAY_SIZE];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = new Element();
        data.setArray(arr);

        arr = new Element[PACKED_ARRAY_SIZE];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = new Element();
        data.setPackedArray(arr);

        return data;
    }

    public Extended2 createExtended2()
    {
        Extended2 data = new Extended2();

        Element[] arr = new Element[ARRAY_SIZE];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = new Element();
        data.setArray(arr);

        arr = new Element[PACKED_ARRAY_SIZE];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = new Element();
        data.setPackedArray(arr);

        return data;
    }

    static final int ARRAY_SIZE = 1;
    static final int PACKED_ARRAY_SIZE = 5;

    static final int ORIGINAL_BIT_SIZE = 8 + 32; // array of Elements of length 1
    static final int EXTENDED1_BIT_SIZE =
            (int)BitPositionUtil.alignTo(8, ORIGINAL_BIT_SIZE) + // align to 8 due to extended
            8 + // varsize
            1 + // is packed
            6 + // max bit number
            32; // first element
    static final int EXTENDED2_BIT_SIZE =
            (int)BitPositionUtil.alignTo(8, EXTENDED1_BIT_SIZE) + // align to 8 due to extended
            1; // auto optional not present

    @Test
    public void defaultConstructor()
    {
        Extended2 data = new Extended2();

        // always present when not read from stream
        assertTrue(data.isPackedArrayPresent());
        assertTrue(data.isOptionalPackedArrayPresent());

        // default initialized
        assertNull(data.getPackedArray());
        assertFalse(data.isOptionalPackedArraySet());
    }

    @Test
    public void fieldConstructor()
    {
        Extended2 data = new Extended2(null, new Element[] {new Element(42)}, null);

        assertTrue(data.isPackedArrayPresent());
        assertEquals(42, data.getPackedArray()[0].getValue());
    }

    @Test
    public void operatorEquality()
    {
        Extended2 data = createExtended2();
        Extended2 equalData = createExtended2();

        CompoundUtil.comparisonOperatorsTest(data, equalData);
    }

    @Test
    public void bitSizeOfExtended1()
    {
        Extended1 data = createExtended1();

        assertEquals(EXTENDED1_BIT_SIZE, data.bitSizeOf());
    }

    @Test
    public void bitSizeOfExtended2()
    {
        Extended2 data = createExtended2();

        assertEquals(EXTENDED2_BIT_SIZE, data.bitSizeOf());
    }

    @Test
    public void writeReadExtended2()
    {
        Extended2 data = createExtended2();

        CompoundUtil.writeReadTest(Extended2.class, data);
    }

    @Test
    public void writeExtended1ReadExtended2()
    {
        Extended1 dataExtended1 = createExtended1();

        BitBuffer bitBuffer = SerializeUtil.serialize(dataExtended1);
        Extended2 readDataExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        assertFalse(readDataExtended2.isOptionalPackedArrayPresent());

        // bit size as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readDataExtended2.bitSizeOf());

        // write as extened1
        bitBuffer = SerializeUtil.serialize(readDataExtended2);
        assertEquals(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

        // read extended1 again
        Extended1 readDataExtended1 = SerializeUtil.deserialize(Extended1.class, bitBuffer);
        assertEquals(dataExtended1, readDataExtended1);

        // make the extended value present
        readDataExtended2.resetOptionalPackedArray();
        assertTrue(readDataExtended2.isOptionalPackedArrayPresent());
        assertFalse(readDataExtended2.isOptionalPackedArraySet()); // optional not present

        // bit size as extended2
        assertEquals(EXTENDED2_BIT_SIZE, readDataExtended2.bitSizeOf());

        // write as extended2
        bitBuffer = SerializeUtil.serialize(readDataExtended2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

        CompoundUtil.writeReadTest(Extended2.class, readDataExtended2);
    }

    @Test
    public void stdHash()
    {
        Extended2 data = createExtended2();
        final int dataHash = -1035706399;
        Extended2 equalData = createExtended2();
        Extended2 diffData = createExtended2();
        diffData.getPackedArray()[diffData.getPackedArray().length - 1].setValue(12);
        final int diffDataHash = -1035706400;

        CompoundUtil.hashTest(data, dataHash, equalData, diffData, diffDataHash);
    }
}
