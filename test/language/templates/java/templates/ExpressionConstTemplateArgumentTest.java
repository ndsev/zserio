package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.expression_const_template_argument.ConstTemplateArgumentHolder;
import templates.expression_const_template_argument.ConstTemplateArgument_LENGTH;

public class ExpressionConstTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final ConstTemplateArgument_LENGTH constTemplateArgument_LENGTH =
                new ConstTemplateArgument_LENGTH(new UnsignedByteArray(10), 10);
        final ConstTemplateArgumentHolder constTemplateArgumentHolder =
                new ConstTemplateArgumentHolder(constTemplateArgument_LENGTH);
        assertTrue(constTemplateArgumentHolder.getConstTemplateArgument().hasExtraField());

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        constTemplateArgumentHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final ConstTemplateArgumentHolder readConstTemplateArgumentHolder = 
                new ConstTemplateArgumentHolder(reader);
        reader.close();
        assertTrue(constTemplateArgumentHolder.equals(readConstTemplateArgumentHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
