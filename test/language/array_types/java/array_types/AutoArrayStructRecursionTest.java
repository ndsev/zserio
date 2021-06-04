package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import array_types.auto_array_struct_recursion.AutoArrayRecursion;

public class AutoArrayStructRecursionTest
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void initializeOffsetsLength1() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void initializeOffsetsLength2() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void readLength1() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void readLength2() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void writeLength1() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeLength2() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH2);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int autoArrayRecursionBitSize = 8 + 8 + numElements * (8 + 8);
        assertEquals(autoArrayRecursionBitSize, autoArrayRecursion.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + 8 + numElements * (8 + 8);
        assertEquals(expectedEndBitPosition, autoArrayRecursion.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeAutoArrayRecursionToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final AutoArrayRecursion autoArrayRecursion = new AutoArrayRecursion(stream);
        stream.close();
        checkAutoArrayRecursion(autoArrayRecursion, numElements);
    }

    private void checkWrite(short numElements) throws IOException, ZserioError
    {
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoArrayRecursion.write(writer);
        writer.close();

        final AutoArrayRecursion readAutoArrayRecursion = new AutoArrayRecursion(file);
        checkAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    private AutoArrayRecursion createAutoArrayRecursion(short numElements)
    {
        final List<AutoArrayRecursion> autoArray = new ArrayList<AutoArrayRecursion>();
        for (short i = 1; i <= numElements; ++i)
        {
            final AutoArrayRecursion element = new AutoArrayRecursion(i,
                    new ObjectArray<AutoArrayRecursion>(0));
            autoArray.add(element);
        }

        return new AutoArrayRecursion((short) 0, new ObjectArray<AutoArrayRecursion>(autoArray));
    }

    private void checkAutoArrayRecursion(AutoArrayRecursion autoArrayRecursion, short numElements)
    {
        assertEquals(0, autoArrayRecursion.getId());
        final ObjectArray<AutoArrayRecursion> autoArray = autoArrayRecursion.getAutoArrayRecursion();
        assertEquals(numElements, autoArray.length());
        for (short i = 1; i <= numElements; ++i)
        {
            final AutoArrayRecursion element = autoArray.elementAt(i - 1);
            assertEquals(i, element.getId());
            assertEquals(0, element.getAutoArrayRecursion().length());
        }
    }

    private void writeAutoArrayRecursionToFile(File file, short numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeUnsignedByte((short) 0);
        writer.writeVarSize(numElements);
        for (short i = 1; i <= numElements; ++i)
        {
            writer.writeUnsignedByte(i);
            writer.writeVarSize(0);
        }

        writer.close();
    }

    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
