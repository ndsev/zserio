package union_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import union_types.union_with_array.TestUnion;
import union_types.union_with_array.Data8;

import org.junit.Test;

public class UnionWithArrayTest
{
    @Test
    public void array8()
    {
        final TestUnion test = new TestUnion();
        test.setArray8(new Data8[4]);
        assertEquals(4, test.getArray8().length);
    }

    @Test
    public void array16()
    {
        final TestUnion test = new TestUnion();
        test.setArray16(new short[4]);
        assertEquals(4, test.getArray16().length);
    }

    @Test
    public void writeReadFile() throws IOException
    {
        {
            final TestUnion testUnion = new TestUnion();
            testUnion.setArray8(createArray8());
            final File file = new File(BLOB_NAME_BASE + "array8.blob");
            final FileBitStreamWriter writer = new FileBitStreamWriter(file);
            testUnion.write(writer);
            writer.close();

            final TestUnion readTestUnion = new TestUnion(file);
            assertEquals(testUnion, readTestUnion);
        }

        {
            final TestUnion testUnion = new TestUnion();
            testUnion.setArray16(createArray16());
            final File file = new File(BLOB_NAME_BASE + "array16.blob");
            final FileBitStreamWriter writer = new FileBitStreamWriter(file);
            testUnion.write(writer);
            writer.close();

            final TestUnion readTestUnion = new TestUnion(file);
            assertEquals(testUnion, readTestUnion);
        }
    }

    private static Data8[] createArray8()
    {
        return new Data8[]{new Data8((byte)-1), new Data8((byte)-2), new Data8((byte)-3), new Data8((byte)-4)};
    }

    private static short[] createArray16()
    {
        return new short[]{-10, -20, -30, -40, -50};
    }

    private static final String BLOB_NAME_BASE = "union_with_array_";
}
