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

import templates.struct_full_name_template_argument.StructFullNameTemplateArgument;
import templates.struct_full_name_template_argument.Storage;
import templates.struct_full_name_template_argument.TemplatedStruct_Storage_A3A4B101;
import templates.struct_full_name_template_argument.TemplatedStruct_Storage_08C0ED6D;

public class StructFullNameTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructFullNameTemplateArgument structFullNameTemplateArgument =
                new StructFullNameTemplateArgument();
        structFullNameTemplateArgument.setStructExternal(new TemplatedStruct_Storage_08C0ED6D(
                new templates.struct_full_name_template_argument.storage.Storage((long)42)));
        structFullNameTemplateArgument.setStructInternal(new TemplatedStruct_Storage_A3A4B101(
                new Storage("string")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structFullNameTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructFullNameTemplateArgument readStructFullNameTemplateArgument =
                new StructFullNameTemplateArgument(reader);
        reader.close();
        assertTrue(structFullNameTemplateArgument.equals(readStructFullNameTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
