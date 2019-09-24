package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.templated_union_field.TemplatedUnionField;
import templates.templated_union_field.TemplatedUnion_uint16_uint32;
import templates.templated_union_field.TemplatedUnion_float32_float64;
import templates.templated_union_field.TemplatedUnion_Compound_uint16_Compound_uint32;
import templates.templated_union_field.Compound_uint16;
import templates.templated_union_field.Compound_Compound_uint16;

public class TemplatedUnionFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedUnionField templatedUnionField = new TemplatedUnionField();

        final TemplatedUnion_uint16_uint32 uintUnion = new TemplatedUnion_uint16_uint32();
        uintUnion.setField1(42);
        templatedUnionField.setUintUnion(uintUnion);

        final TemplatedUnion_float32_float64 floatUnion = new TemplatedUnion_float32_float64();
        floatUnion.setField2(42.0f);
        templatedUnionField.setFloatUnion(floatUnion);

        final TemplatedUnion_Compound_uint16_Compound_uint32 compoundUnion =
                new TemplatedUnion_Compound_uint16_Compound_uint32();
        compoundUnion.setField3(new Compound_Compound_uint16(new Compound_uint16(13)));
        templatedUnionField.setCompoundUnion(compoundUnion);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedUnionField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final TemplatedUnionField readTemplatedUnionField = new TemplatedUnionField(reader);
        reader.close();
        assertTrue(templatedUnionField.equals(readTemplatedUnionField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
