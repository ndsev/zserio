package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;
import zserio.runtime.io.Writer;

import choice_types.choice_compatibility_check.ChoiceCompatibilityCheckVersion1;
import choice_types.choice_compatibility_check.ChoiceCompatibilityCheckVersion2;
import choice_types.choice_compatibility_check.ChoiceVersion1;
import choice_types.choice_compatibility_check.ChoiceVersion2;
import choice_types.choice_compatibility_check.CoordXY;
import choice_types.choice_compatibility_check.CoordXYZ;
import choice_types.choice_compatibility_check.EnumVersion1;
import choice_types.choice_compatibility_check.EnumVersion2;
import choice_types.choice_compatibility_check.HolderVersion1;
import choice_types.choice_compatibility_check.HolderVersion2;

public class ChoiceCompatibilityCheckTest
{
    @Test
    public void writeVersion1ReadVersion1() throws IOException
    {
        final ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1 = create(createArrayVersion1());
        final ChoiceCompatibilityCheckVersion1 readChoiceCompatibilityCheckVersion1 =
                writeReadVersion1(choiceCompatibilityCheckVersion1);
        assertEquals(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1);
    }

    @Test
    public void writeVersion1ReadVersion2() throws IOException
    {
        final ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1 = create(createArrayVersion1());
        final ChoiceCompatibilityCheckVersion2 readChoiceCompatibilityCheckVersion2 =
                writeReadVersion2(choiceCompatibilityCheckVersion1);

        final HolderVersion2[] expectedArrayVersion2 = createArrayVersion2WithVersion1Fields();
        assertArrayEquals(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getArray());
        assertArrayEquals(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion1() throws IOException
    {
        final ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2 =
                create(createArrayVersion2WithVersion1Fields());
        final ChoiceCompatibilityCheckVersion1 readChoiceCompatibilityCheckVersion1 =
                writeReadVersion1(choiceCompatibilityCheckVersion2);

        final HolderVersion1[] expectedArrayVersion1 = createArrayVersion1();
        assertArrayEquals(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getArray());
        assertArrayEquals(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion2() throws IOException
    {
        final ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2 = create(createArrayVersion2());
        final ChoiceCompatibilityCheckVersion2 readChoiceCompatibilityCheckVersion2 =
                writeReadVersion2(choiceCompatibilityCheckVersion2);
        assertEquals(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2);
    }

    @Test
    public void writeVersion1ReadVersion1File() throws IOException
    {
        final ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1 = create(createArrayVersion1());
        final ChoiceCompatibilityCheckVersion1 readChoiceCompatibilityCheckVersion1 =
                writeReadVersion1File(choiceCompatibilityCheckVersion1, "version1_version1");
        assertEquals(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1);
    }

    @Test
    public void writeVersion1ReadVersion2File() throws IOException
    {
        final ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1 = create(createArrayVersion1());
        final ChoiceCompatibilityCheckVersion2 readChoiceCompatibilityCheckVersion2 =
                writeReadVersion2File(choiceCompatibilityCheckVersion1, "version1_version2");

        final HolderVersion2[] expectedArrayVersion2 = createArrayVersion2WithVersion1Fields();
        assertArrayEquals(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getArray());
        assertArrayEquals(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion1File() throws IOException
    {
        final ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2 =
                create(createArrayVersion2WithVersion1Fields());
        final ChoiceCompatibilityCheckVersion1 readChoiceCompatibilityCheckVersion1 =
                writeReadVersion1File(choiceCompatibilityCheckVersion2, "version2_version1");

        final HolderVersion1[] expectedArrayVersion1 = createArrayVersion1();
        assertArrayEquals(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getArray());
        assertArrayEquals(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getPackedArray());
    }

    @Test
    public void writeVersion2ReadVersion2File() throws IOException
    {
        final ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2 = create(createArrayVersion2());
        final ChoiceCompatibilityCheckVersion2 readChoiceCompatibilityCheckVersion2 =
                writeReadVersion2File(choiceCompatibilityCheckVersion2, "version2_version2");
        assertEquals(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2);
    }

    private ChoiceCompatibilityCheckVersion1 create(HolderVersion1 array[])
    {
        return new ChoiceCompatibilityCheckVersion1(array, array);
    }

    private ChoiceCompatibilityCheckVersion2 create(HolderVersion2 array[])
    {
        return new ChoiceCompatibilityCheckVersion2(array, array);
    }

    private HolderVersion1[] createArrayVersion1()
    {
        return new HolderVersion1[] {createHolderVersion1(EnumVersion1.COORD_XY, 0),
                createHolderVersion1(EnumVersion1.TEXT, 1), createHolderVersion1(EnumVersion1.COORD_XY, 2),
                createHolderVersion1(EnumVersion1.TEXT, 3)};
    }

    private HolderVersion2[] createArrayVersion2WithVersion1Fields()
    {
        return new HolderVersion2[] {createHolderVersion2(EnumVersion2.COORD_XY, 0),
                createHolderVersion2(EnumVersion2.TEXT, 1), createHolderVersion2(EnumVersion2.COORD_XY, 2),
                createHolderVersion2(EnumVersion2.TEXT, 3)};
    }

    private HolderVersion2[] createArrayVersion2()
    {
        final HolderVersion2[] arrayVersion2WithVersion1Fields = createArrayVersion2WithVersion1Fields();
        return new HolderVersion2[] {arrayVersion2WithVersion1Fields[0], arrayVersion2WithVersion1Fields[1],
                arrayVersion2WithVersion1Fields[2], arrayVersion2WithVersion1Fields[3],
                createHolderVersion2(EnumVersion2.COORD_XYZ, 4),
                createHolderVersion2(EnumVersion2.COORD_XYZ, 5),
                createHolderVersion2(EnumVersion2.COORD_XYZ, 6)};
    }

    private HolderVersion1 createHolderVersion1(EnumVersion1 selector, int index)
    {
        final ChoiceVersion1 choice = new ChoiceVersion1(selector);
        if (selector == EnumVersion1.COORD_XY)
            choice.setCoordXY(new CoordXY(10L * index, 20L * index));
        else
            choice.setText("text" + index);

        return new HolderVersion1(selector, choice);
    }

    private HolderVersion2 createHolderVersion2(EnumVersion2 selector, int index)
    {
        final ChoiceVersion2 choice = new ChoiceVersion2(selector);
        if (selector == EnumVersion2.COORD_XY)
            choice.setCoordXY(new CoordXY(10L * index, 20L * index));
        else if (selector == EnumVersion2.TEXT)
            choice.setText("text" + index);
        else
            choice.setCoordXYZ(new CoordXYZ(10L * index, 20L * index, 1.1 * index));

        return new HolderVersion2(selector, choice);
    }

    private ChoiceCompatibilityCheckVersion1 writeReadVersion1(Writer choiceCompatibilityCheck)
            throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceCompatibilityCheck.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final ChoiceCompatibilityCheckVersion1 readChoice = new ChoiceCompatibilityCheckVersion1(reader);
        reader.close();

        return readChoice;
    }

    private ChoiceCompatibilityCheckVersion2 writeReadVersion2(Writer choiceCompatibilityCheck)
            throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceCompatibilityCheck.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final ChoiceCompatibilityCheckVersion2 readChoice = new ChoiceCompatibilityCheckVersion2(reader);
        reader.close();

        return readChoice;
    }

    private ChoiceCompatibilityCheckVersion1 writeReadVersion1File(
            Writer choiceCompatibilityCheck, String variant) throws IOException
    {
        final File file = new File(BLOB_NAME_BASE + variant + ".blob");

        SerializeUtil.serializeToFile(choiceCompatibilityCheck, file);
        final ChoiceCompatibilityCheckVersion1 readChoice =
                SerializeUtil.deserializeFromFile(ChoiceCompatibilityCheckVersion1.class, file);

        return readChoice;
    }

    private ChoiceCompatibilityCheckVersion2 writeReadVersion2File(
            Writer choiceCompatibilityCheck, String variant) throws IOException
    {
        final File file = new File(BLOB_NAME_BASE + variant + ".blob");

        SerializeUtil.serializeToFile(choiceCompatibilityCheck, file);
        final ChoiceCompatibilityCheckVersion2 readChoice =
                SerializeUtil.deserializeFromFile(ChoiceCompatibilityCheckVersion2.class, file);

        return readChoice;
    }

    private static final String BLOB_NAME_BASE = "choice_compatibility_check_";
}
