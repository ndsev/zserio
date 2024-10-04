package builtin_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

import builtin_types.bitfield_function_length.Container;

public class BitFieldFunctionLengthTest
{
    @Test
    public void bitSizeOf()
    {
        final Container container = createContainer();
        final int expectedBitSizeOfContainer = 64 + 7 * 64 + 7 + 13 + 13;
        assertEquals(expectedBitSizeOfContainer, container.bitSizeOf());
    }

    @Test
    public void readWrite() throws IOException
    {
        final Container container = createContainer();
        SerializeUtil.serializeToFile(container, TEST_FILE);
        final Container readContainer = SerializeUtil.deserializeFromFile(Container.class, TEST_FILE);
        assertEquals(container, readContainer);
    }

    private Container createContainer()
    {
        return new Container(BigInteger.valueOf(0xDEAD), // id
                new BigInteger[] {BigInteger.valueOf(0xDEAD1), BigInteger.valueOf(0xDEAD2),
                        BigInteger.valueOf(0xDEAD3), BigInteger.valueOf(0xDEAD4), BigInteger.valueOf(0xDEAD5),
                        BigInteger.valueOf(0xDEAD6), BigInteger.valueOf(0xDEAD7)}, // array[7]
                (byte)0x3F, // bitField3 (7 bits)
                (short)0x1FFF, // bitField4 (0xDEAD & 0x0F = 0xD = 13 bits)
                (int)0x1FFF // bitField5 (0xDEAD % 32 = 13 bits)
        );
    }

    private static final File TEST_FILE = new File("bit_field_function_length.blob");
}
