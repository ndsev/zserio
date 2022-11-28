package functions.structure_optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureOptionalTest
{
    @Test
    public void checkDefaultValueConsumerCreator() throws IOException
    {
        checkValueConsumerCreator(DEFAULT_VALUE, EXTERNAL_VALUE);
    }

    @Test
    public void checkExtenalValueConsumerCreator() throws IOException
    {
        checkValueConsumerCreator(INVALID_DEFAULT_VALUE, EXTERNAL_VALUE);
    }

    private byte calculateValue(byte defaultValue, byte externalValue)
    {
        return (defaultValue != INVALID_DEFAULT_VALUE) ? defaultValue : externalValue;
    }

    private byte[] writeValueConsumerCreatorToByteArray(byte defaultValue, byte externalValue)
            throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(defaultValue, 4);
        if (defaultValue == INVALID_DEFAULT_VALUE)
            writer.writeBits(externalValue, 4);
        writer.writeBool((calculateValue(defaultValue, externalValue) < SMALL_VALUE_THRESHOLD));
        writer.close();

        return writer.toByteArray();
    }

    private ValueConsumerCreator createValueConsumerCreator(byte defaultValue, byte externalValue)
    {
        final ValueConsumerCreator valueConsumerCreator = new ValueConsumerCreator();

        final ValueCalculator valueCalculator = new ValueCalculator();
        valueCalculator.setDefaultValue(defaultValue);
        if (defaultValue == INVALID_DEFAULT_VALUE)
            valueCalculator.setExternalValue(externalValue);
        valueConsumerCreator.setValueCalculator(valueCalculator);

        final ValueConsumer valueConsumer = new ValueConsumer(valueCalculator.funcValue());
        valueConsumer.setIsSmall(calculateValue(defaultValue, externalValue) < SMALL_VALUE_THRESHOLD);
        valueConsumerCreator.setValueConsumer(valueConsumer);

        return valueConsumerCreator;
    }

    private void checkValueConsumerCreator(byte defaultValue, byte externalValue) throws IOException
    {
        final ValueConsumerCreator valueConsumerCreator = createValueConsumerCreator(defaultValue,
                externalValue);
        assertEquals(calculateValue(defaultValue, externalValue),
                valueConsumerCreator.getValueCalculator().funcValue());

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        valueConsumerCreator.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();

        final byte[] expectedByteArray = writeValueConsumerCreatorToByteArray(defaultValue, externalValue);
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writtenByteArray, writer.getBitPosition());
        final ValueConsumerCreator readValueConsumerCreator = new ValueConsumerCreator(reader);
        assertEquals(valueConsumerCreator, readValueConsumerCreator);
    }

    private static byte INVALID_DEFAULT_VALUE = 0;
    private static byte DEFAULT_VALUE = 1;
    private static byte EXTERNAL_VALUE = 2;
    private static byte SMALL_VALUE_THRESHOLD = 8;
}
