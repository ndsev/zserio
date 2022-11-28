package union_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.SerializeUtil;

import union_types.union_with_array.TestUnion;
import union_types.union_with_array.Data8;

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
            SerializeUtil.serializeToFile(testUnion, file);

            final TestUnion readTestUnion = SerializeUtil.deserializeFromFile(TestUnion.class, file);
            assertEquals(testUnion, readTestUnion);
        }

        {
            final TestUnion testUnion = new TestUnion();
            testUnion.setArray16(createArray16());
            final File file = new File(BLOB_NAME_BASE + "array16.blob");
            SerializeUtil.serializeToFile(testUnion, file);

            final TestUnion readTestUnion = SerializeUtil.deserializeFromFile(TestUnion.class, file);
            assertEquals(testUnion, readTestUnion);
        }
    }

    @Test
    public void hashCodeMethod()
    {
        TestUnion testUnion1 = new TestUnion();
        TestUnion testUnion2 = new TestUnion();
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
        testUnion1.setArray8(createArray8());
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());
        testUnion2.setArray8(createArray8());
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
        testUnion2.getArray8()[0].setData((byte)0);
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());
        testUnion2.setArray16(createArray16());
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(87386744, testUnion1.hashCode());
        assertEquals(1575145265, testUnion2.hashCode());

        testUnion1.setArray16(createArray16());
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
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
