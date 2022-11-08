package union_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import union_types.union_compatibility_check.UnionCompatibilityCheckVersion1;
import union_types.union_compatibility_check.UnionCompatibilityCheckVersion2;
import union_types.union_compatibility_check.UnionVersion1;
import union_types.union_compatibility_check.UnionVersion2;
import union_types.union_compatibility_check.CoordXY;
import union_types.union_compatibility_check.CoordXYZ;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;
import zserio.runtime.io.Writer;

public class UnionCompatibilityCheckTest
{
    @Test
    public void writeVersion1ReadVersion1() throws IOException
    {
        final UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1 = create(createArrayVersion1());
        final UnionCompatibilityCheckVersion1 readUnionCompatibilityCheckVersion1 = writeReadVersion1(
                unionCompatibilityCheckVersion1);
        assertEquals(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1);
    }

    @Test
    public void writeVersion1ReadVersion2() throws IOException
    {
        final UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1 = create(createArrayVersion1());
        final UnionCompatibilityCheckVersion2 readUnionCompatibilityCheckVersion2 = writeReadVersion2(
                unionCompatibilityCheckVersion1);

        final UnionVersion2[] expectedArrayVersion2 = createArrayVersion2WithVersion1Fields();
        assertArrayEquals(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getArray());
        assertArrayEquals(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion1() throws IOException
    {
        final UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2 =
                create(createArrayVersion2WithVersion1Fields());
        final UnionCompatibilityCheckVersion1 readUnionCompatibilityCheckVersion1 = writeReadVersion1(
                unionCompatibilityCheckVersion2);

        final UnionVersion1[] expectedArrayVersion1 = createArrayVersion1();
        assertArrayEquals(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getArray());
        assertArrayEquals(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion2() throws IOException
    {
        final UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2 = create(createArrayVersion2());
        final UnionCompatibilityCheckVersion2 readUnionCompatibilityCheckVersion2 = writeReadVersion2(
                unionCompatibilityCheckVersion2);
        assertEquals(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2);
    }

    @Test
    public void writeVersion1ReadVersion1File() throws IOException
    {
        final UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1 = create(createArrayVersion1());
        final UnionCompatibilityCheckVersion1 readUnionCompatibilityCheckVersion1 = writeReadVersion1File(
                unionCompatibilityCheckVersion1, "version1_version1");
        assertEquals(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1);
    }

    @Test
    public void writeVersion1ReadVersion2File() throws IOException
    {
        final UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1 = create(createArrayVersion1());
        final UnionCompatibilityCheckVersion2 readUnionCompatibilityCheckVersion2 = writeReadVersion2File(
                unionCompatibilityCheckVersion1, "version1_version2");

        final UnionVersion2[] expectedArrayVersion2 = createArrayVersion2WithVersion1Fields();
        assertArrayEquals(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getArray());
        assertArrayEquals(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion1File() throws IOException
    {
        final UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2 =
                create(createArrayVersion2WithVersion1Fields());
        final UnionCompatibilityCheckVersion1 readUnionCompatibilityCheckVersion1 = writeReadVersion1File(
                unionCompatibilityCheckVersion2, "version2_version1");

        final UnionVersion1[] expectedArrayVersion1 = createArrayVersion1();
        assertArrayEquals(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getArray());
        assertArrayEquals(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion2File() throws IOException
    {
        final UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2 = create(createArrayVersion2());
        final UnionCompatibilityCheckVersion2 readUnionCompatibilityCheckVersion2 = writeReadVersion2File(
                unionCompatibilityCheckVersion2, "version2_version2");
        assertEquals(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2);
    }

    private UnionCompatibilityCheckVersion1 create(UnionVersion1 array[])
    {
        return new UnionCompatibilityCheckVersion1(array, array);
    }

    private UnionCompatibilityCheckVersion2 create(UnionVersion2 array[])
    {
        return new UnionCompatibilityCheckVersion2(array, array);
    }

    private UnionVersion1[] createArrayVersion1()
    {
        return new UnionVersion1[] {
            createUnionVersion1(0),
            createUnionVersion1(1),
            createUnionVersion1(2),
            createUnionVersion1(3)
        };
    }

    private UnionVersion2[] createArrayVersion2WithVersion1Fields()
    {
        return new UnionVersion2[] {
            createUnionVersion2(0),
            createUnionVersion2(1),
            createUnionVersion2(2),
            createUnionVersion2(3)
        };
    }

    private UnionVersion2[] createArrayVersion2()
    {
        final UnionVersion2[] arrayVersion2WithVersion1Fields = createArrayVersion2WithVersion1Fields();
        return new UnionVersion2[] {
            arrayVersion2WithVersion1Fields[0],
            arrayVersion2WithVersion1Fields[1],
            arrayVersion2WithVersion1Fields[2],
            arrayVersion2WithVersion1Fields[3],
            createUnionCoordXYZ(4),
            createUnionCoordXYZ(5),
            createUnionCoordXYZ(6)
        };
    }

    private UnionVersion1 createUnionVersion1(int index)
    {
        final UnionVersion1 union = new UnionVersion1();
        if (index % 2 == 0)
            union.setCoordXY(new CoordXY(10L * index, 20L * index));
        else
            union.setText("text" + index);

        return union;
    }

    private UnionVersion2 createUnionVersion2(int index)
    {
        final UnionVersion2 union = new UnionVersion2();
        if (index % 2 == 0)
            union.setCoordXY(new CoordXY(10L * index, 20L * index));
        else
            union.setText("text" + index);

        return union;
    }

    private UnionVersion2 createUnionCoordXYZ(int index)
    {
        final UnionVersion2 union = new UnionVersion2();
        union.setCoordXYZ(new CoordXYZ(10L * index, 20L * index, 1.1 * index));
        return union;
    }

    private UnionCompatibilityCheckVersion1 writeReadVersion1(
            Writer unionCompatibilityCheck) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        unionCompatibilityCheck.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final UnionCompatibilityCheckVersion1 readUnion = new UnionCompatibilityCheckVersion1(reader);
        reader.close();

        return readUnion;
    }

    private UnionCompatibilityCheckVersion2 writeReadVersion2(
            Writer unionCompatibilityCheck) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        unionCompatibilityCheck.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final UnionCompatibilityCheckVersion2 readUnion = new UnionCompatibilityCheckVersion2(reader);
        reader.close();

        return readUnion;
    }

    private UnionCompatibilityCheckVersion1 writeReadVersion1File(
            Writer unionCompatibilityCheck, String variant) throws IOException
    {
        final File file = new File(BLOB_NAME_BASE + variant + ".blob");

        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        unionCompatibilityCheck.write(writer);
        writer.close();

        final FileBitStreamReader reader = new FileBitStreamReader(file);
        final UnionCompatibilityCheckVersion1 readUnion = new UnionCompatibilityCheckVersion1(reader);
        reader.close();

        return readUnion;
    }

    private UnionCompatibilityCheckVersion2 writeReadVersion2File(
            Writer unionCompatibilityCheck, String variant) throws IOException
    {
        final File file = new File(BLOB_NAME_BASE + variant + ".blob");

        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        unionCompatibilityCheck.write(writer);
        writer.close();

        final FileBitStreamReader reader = new FileBitStreamReader(file);
        final UnionCompatibilityCheckVersion2 readUnion = new UnionCompatibilityCheckVersion2(reader);
        reader.close();

        return readUnion;
    }

    private static final String BLOB_NAME_BASE = "union_compatibility_check_";
}
