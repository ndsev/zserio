package parameterized_types.compound_and_field_with_same_param;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

// this test is mainly for C++, so just check that it is ok
public class CompoundAndFieldWithSameParamTest
{
    @Test
    public void compoundReadConstructor() throws IOException
    {
        final File file = new File("test.bin");
        writeCompoundReadToFile(file);

        BitStreamReader stream = new FileBitStreamReader(file);
        final CompoundRead compoundRead = new CompoundRead(stream, PARAM);
        assertEquals(FIELD1, compoundRead.getField1().getValue());
        assertEquals(FIELD2, compoundRead.getField2().getValue());

        stream = new FileBitStreamReader(file);
        final CompoundReadTest compoundReadTest = new CompoundReadTest(stream);
        assertEquals(PARAM, compoundReadTest.getCompoundRead().getParam());
        assertEquals(FIELD1, compoundReadTest.getCompoundRead().getField1().getValue());
        assertEquals(FIELD2, compoundReadTest.getCompoundRead().getField2().getValue());
    }

    @Test
    public void compoundPackingConstructor() throws IOException
    {
        final File file = new File("test.bin");
        writeCompoundPackingToFile(file);

        BitStreamReader stream = new FileBitStreamReader(file);
        final CompoundPacking compoundPacking = new CompoundPacking(stream, PARAM);
        assertEquals(FIELD1, compoundPacking.getField1().getValue());
        assertEquals(FIELD2, compoundPacking.getField2().getValue());
        assertEquals(FIELD3, compoundPacking.getField3().getValue());

        stream = new FileBitStreamReader(file);
        final CompoundPackingTest compoundPackingTest = new CompoundPackingTest(stream);
        assertEquals(PARAM, compoundPackingTest.getCompoundPacking().getParam());
        assertEquals(FIELD1, compoundPackingTest.getCompoundPacking().getField1().getValue());
        assertEquals(FIELD2, compoundPackingTest.getCompoundPacking().getField2().getValue());
        assertEquals(FIELD3, compoundPackingTest.getCompoundPacking().getField3().getValue());
    }

    private void writeCompoundReadToFile(File file) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);
        stream.writeBits(FIELD1, 32);
        stream.writeBits(FIELD2, 32);
        stream.close();
    }

    private void writeCompoundPackingToFile(File file) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);
        stream.writeBits(FIELD1, 32);
        stream.writeBits(FIELD2, 32);
        stream.writeBits(FIELD3, 32);
        stream.close();
    }

    private static final int PARAM = 10;
    private static final long FIELD1 = 1;
    private static final long FIELD2 = 9;
    private static final long FIELD3 = 5;
}
