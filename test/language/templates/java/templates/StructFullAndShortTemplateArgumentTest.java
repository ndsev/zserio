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

import templates.struct_full_and_short_template_argument.StructFullNameTemplateArgument;
import templates.struct_full_and_short_template_argument.templated_struct.StructShortNameTemplateArgument;
import templates.struct_full_and_short_template_argument.templated_struct.Storage;
import templates.struct_full_and_short_template_argument.templated_struct.TemplatedStruct_Storage;

public class StructFullAndShortTemplateArgumentTest
{
    @Test
    public void readWriteFull() throws IOException
    {
        final StructFullNameTemplateArgument structFullNameTemplateArgument =
                new StructFullNameTemplateArgument(new TemplatedStruct_Storage(new Storage("String")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structFullNameTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructFullNameTemplateArgument readStructFullNameTemplateArgument =
                new StructFullNameTemplateArgument(reader);
        reader.close();
        assertTrue(structFullNameTemplateArgument.equals(readStructFullNameTemplateArgument));
    }

    @Test
    public void readWriteShort() throws IOException
    {
        final StructShortNameTemplateArgument structShortNameTemplateArgument =
                new StructShortNameTemplateArgument(new TemplatedStruct_Storage(new Storage("String")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structShortNameTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructShortNameTemplateArgument readStructShortNameTemplateArgument =
                new StructShortNameTemplateArgument(reader);
        reader.close();
        assertTrue(structShortNameTemplateArgument.equals(readStructShortNameTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
