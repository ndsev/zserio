package zserio.runtime.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.array.Array;
import zserio.runtime.array.ArrayTraits;
import zserio.runtime.array.ArrayType;
import zserio.runtime.array.RawArray;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class ValidationBitStreamReaderTest
{
    @Test
    public void validationWithReadOnly() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeBits(2, 2);
            writer.writeBits(7, 3);
            writer.writeBits(9, 4);
            writer.writeBits(10, 5);

            final byte[] originalStream = writer.toByteArray();

            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // put rubbish at the end position which is not used
            changedStream[changedStream.length - 1] |= 0x01;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertEquals(1, reader.readBits(1));
                assertEquals(2, reader.readBits(2));
                assertEquals(7, reader.readBits(3));
                assertEquals(9, reader.readBits(4));
                assertEquals(10, reader.readBits(5));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithAlignTo() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.alignTo(2);
            writer.writeBits(2, 2);
            writer.alignTo(4);
            writer.writeBits(7, 3);
            writer.alignTo(4);
            writer.writeBits(9, 4);
            writer.alignTo(8);
            writer.writeBits(10, 5);

            final byte[] originalStream = writer.toByteArray();

            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // put rubbish to the aligned dummy bits and at the end
            changedStream[0] |= 0x40;
            changedStream[0] |= 0x01;
            changedStream[1] |= 0x0F;
            changedStream[2] |= 0x03;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertEquals(1, reader.readBits(1));
                reader.alignTo(2);
                assertEquals(2, reader.readBits(2));
                reader.alignTo(4);
                assertEquals(7, reader.readBits(3));
                reader.alignTo(4);
                assertEquals(9, reader.readBits(4));
                reader.alignTo(8);
                assertEquals(10, reader.readBits(5));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithSkipBits() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeBits(0, 1); // skip
            writer.writeBits(2, 2);
            writer.writeBits(0, 2); // skip
            writer.writeBits(7, 3);
            writer.writeBits(0, 3); // skip
            writer.writeBits(9, 4);
            writer.writeBits(0, 4); // skip
            writer.writeBits(10, 5);
            writer.writeBits(0, 5); // skip

            final byte[] originalStream = writer.toByteArray();

            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // put rubbish to the skipped dummy bits and at the end
            changedStream[0] |= 0x40;
            changedStream[0] |= 0x0C;
            changedStream[1] |= 0x70;
            changedStream[2] |= 0xF0;
            changedStream[3] |= 0x7C;
            changedStream[3] |= 0x03;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertEquals(1, reader.readBits(1));
                reader.setBitPosition(reader.getBitPosition() + 1);
                assertEquals(2, reader.readBits(2));
                reader.setBitPosition(reader.getBitPosition() + 2);
                assertEquals(7, reader.readBits(3));
                reader.setBitPosition(reader.getBitPosition() + 3);
                assertEquals(9, reader.readBits(4));
                reader.setBitPosition(reader.getBitPosition() + 4);
                assertEquals(10, reader.readBits(5));
                reader.setBitPosition(reader.getBitPosition() + 5);

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithSkipBytes() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeBits(0, 8); // skip
            writer.writeBits(2, 2);
            writer.writeBits(0, 8); // skip
            writer.writeBits(7, 3);

            final byte[] originalStream = writer.toByteArray();

            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // put rubbish to the skipped dummy bits and at the end
            changedStream[0] |= 0x7F;
            changedStream[1] |= 0x80;
            changedStream[1] |= 0x1F;
            changedStream[2] |= 0xE0;
            changedStream[2] |= 0x03;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertEquals(1, reader.readBits(1));
                reader.setBitPosition(reader.getBitPosition() + 8);
                assertEquals(2, reader.readBits(2));
                reader.setBitPosition(reader.getBitPosition() + 8);
                assertEquals(7, reader.readBits(3));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithSetPosition() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeBits(2, 2);
            writer.writeBits(7, 3);
            writer.writeBits(9, 4);
            writer.writeBits(10, 5);

            final byte[] originalStream = writer.toByteArray();

            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // put rubbish at the end position which is not used
            changedStream[changedStream.length - 1] |= 0x01;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                reader.readBits(16);
                reader.setBitPosition(0);
                assertEquals(1, reader.readBits(1));
                assertEquals(2, reader.readBits(2));
                assertEquals(7, reader.readBits(3));
                assertEquals(9, reader.readBits(4));
                assertEquals(10, reader.readBits(5));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithImplicitArray() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(0xAB, 8);
            writer.writeBits(0xCD, 8);

            final byte[] originalStream = writer.toByteArray();

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(originalStream))
            {
                final RawArray rawArray = new RawArray.ShortRawArray();
                final ArrayTraits arrayTraits = new ArrayTraits.BitFieldShortArrayTraits(8);
                final Array readArray = new Array(rawArray, arrayTraits, ArrayType.IMPLICIT);
                readArray.read(reader);
                final short[] readRawArray = readArray.getRawArray();
                assertEquals(0xAB, readRawArray[0]);
                assertEquals(0xCD, readRawArray[1]);

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithNan() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeFloat16(Float.NaN);
            writer.writeBits(2, 2);

            final byte[] originalStream = writer.toByteArray();
            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // change NaN to binary represantation which is not used by writer
            changedStream[0] |= 0x40;
            changedStream[1] |= 0xFF;
            changedStream[2] |= 0x80;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertEquals(1, reader.readBits(1));
                assertTrue(Float.isNaN(reader.readFloat16()));
                assertEquals(2, reader.readBits(2));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }

    @Test
    public void validationWithNanAndSetPosition() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(1, 1);
            writer.writeFloat16(Float.NaN);
            writer.writeBits(2, 2);

            final byte[] originalStream = writer.toByteArray();
            final byte[] changedStream = new byte[originalStream.length];
            System.arraycopy(originalStream, 0, changedStream, 0, changedStream.length);
            // change NaN to binary representation which is not used by writer
            changedStream[0] |= 0x40;
            changedStream[1] |= 0xFF;
            changedStream[2] |= 0x80;

            try (final ValidationBitStreamReader reader = new ValidationBitStreamReader(changedStream))
            {
                assertTrue(Float.isNaN(reader.readFloat16()));
                reader.setBitPosition(0);

                assertEquals(1, reader.readBits(1));
                assertTrue(Float.isNaN(reader.readFloat16()));
                assertEquals(2, reader.readBits(2));

                final byte[] maskedStream = reader.toMaskedByteArray();
                assertTrue(Arrays.equals(originalStream, maskedStream));
            }
        }
    }
}
