package parameterized_types.fixed_and_variable_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class FixedAndVariableParamTest
{
    @Test
    public void writeReadFile() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE, EXTRA_LIMIT,
                LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        SerializeUtil.serializeToFile(fixedAndVariableParam, BLOB_NAME);

        final FixedAndVariableParam readFixedAndVariableParam = SerializeUtil.deserializeFromFile(
                FixedAndVariableParam.class, BLOB_NAME);
        assertEquals(fixedAndVariableParam, readFixedAndVariableParam);
    }

    @Test
    public void writeRead() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE, EXTRA_LIMIT,
                LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        final BitBuffer bitBuffer = SerializeUtil.serialize(fixedAndVariableParam);
        checkFixedAndVariableParamInBitBuffer(bitBuffer, fixedAndVariableParam,
                ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);

        final FixedAndVariableParam readFixedAndVariableParam = SerializeUtil.deserialize(
                FixedAndVariableParam.class, bitBuffer);
        assertEquals(fixedAndVariableParam, readFixedAndVariableParam);
    }

    @Test
    public void writeFailureWrongArraySize() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(WRONG_ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    @Test
    public void writeFailureWrongExtraLimit() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        fixedAndVariableParam.setExtraLimit(WRONG_EXTRA_LIMIT);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    @Test
    public void writeFailureWrongLimitHolder() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        final LimitHolder limitHolder = new LimitHolder(LIMIT);
        fixedAndVariableParam.setLimitHolder(limitHolder);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    @Test
    public void writeFailureWrongColor() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        fixedAndVariableParam.setColor(WRONG_COLOR);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    @Test
    public void writeFailureWrongAccess() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        fixedAndVariableParam.setAccess(WRONG_ACCESS);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    @Test
    public void writeFailureWrongFloatValue() throws IOException
    {
        final FixedAndVariableParam fixedAndVariableParam = createFixedAndVariableParam(ARRAY_SIZE,
                EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
        fixedAndVariableParam.setFloatValue(WRONG_FLOAT_VALUE);

        try (final BitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(ZserioError.class, () -> fixedAndVariableParam.write(writer));
        }
    }

    private ArrayHolder createArrayHolder(int size, short extraLimit, LimitHolder limitHolder, Color color,
            Access access, float floatValue)
    {
        final BigInteger[] array = new BigInteger[size];
        for (int i = 0; i < size; ++i)
            array[i] = BigInteger.valueOf(i * i);
        final boolean hasBlack = (color == Color.BLACK);
        final boolean hasRead = (access.and(Access.Values.READ)).equals(Access.Values.READ);
        final boolean hasFloatBiggerThanOne = (floatValue > 1.0);
        return new ArrayHolder(size, extraLimit, limitHolder, color, access, floatValue, array, EXTRA_VALUE,
                hasBlack, hasRead, hasFloatBiggerThanOne);
    }

    private FixedAndVariableParam createFixedAndVariableParam(int size, short extraLimit, short limit,
            Color color, Access access, float floatValue)
    {
        final LimitHolder limitHolder = new LimitHolder(limit);
        final ArrayHolder arrayHolder = createArrayHolder(size, extraLimit, limitHolder, color, access,
                floatValue);

        return new FixedAndVariableParam(extraLimit, limitHolder, color, access, floatValue, arrayHolder);
    }

    private void checkArrayHolderInStream(BitStreamReader reader, ArrayHolder arrayHolder, int size,
            short extraLimit, LimitHolder limitHolder, Color color, Access access, float floatValue)
                    throws IOException
    {
        assertEquals(arrayHolder.getSize(), size);
        assertEquals(arrayHolder.getExtraLimit(), extraLimit);
        assertEquals(arrayHolder.getLimitHolder(), limitHolder);
        assertEquals(arrayHolder.getColor(), color);
        assertEquals(arrayHolder.getAccess(), access);
        assertEquals(arrayHolder.getFloatValue(), floatValue);

        for (int i = 0; i < size; ++i)
            assertEquals(arrayHolder.getArray()[i], reader.readVarUInt());
        assertEquals(arrayHolder.getExtraValue(), reader.readBits(3));
    }

    private void checkFixedAndVariableParamInBitBuffer(BitBuffer bitBuffer,
            FixedAndVariableParam fixedAndVariableParam, int size, short extraLimit, short limit, Color color,
            Access access, float floatValue) throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            assertEquals(extraLimit, reader.readBits(8));
            assertEquals(limit, reader.readBits(8));
            assertEquals(color.getValue(), reader.readBits(2));
            assertEquals(access.getValue(), reader.readBits(4));
            assertEquals(floatValue, reader.readFloat16());
            final ArrayHolder arrayHolder = fixedAndVariableParam.getArrayHolder();
            final LimitHolder limitHolder = fixedAndVariableParam.getLimitHolder();
            checkArrayHolderInStream(reader, arrayHolder, size, extraLimit, limitHolder, color, access,
                    floatValue);
        }
    }

    private static final String BLOB_NAME = "fixed_and_variable_param.blob";

    private static final int ARRAY_SIZE = 1000;
    private static final int WRONG_ARRAY_SIZE = 1001;
    private static final byte EXTRA_VALUE = 0x05;
    private static final short EXTRA_LIMIT = 0x05;
    private static final short WRONG_EXTRA_LIMIT = 0x06;
    private static final short LIMIT = 0x06;
    private static final Color COLOR = Color.BLACK;
    private static final Color WRONG_COLOR = Color.WHITE;
    private static final Access ACCESS = Access.Values.READ;
    private static final Access WRONG_ACCESS = Access.Values.WRITE;
    private static final float FLOAT_VALUE = 2.0f;
    private static final float WRONG_FLOAT_VALUE = 1.0f;
}
