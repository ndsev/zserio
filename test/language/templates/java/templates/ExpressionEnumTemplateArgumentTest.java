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

import templates.expression_enum_template_argument.EnumTemplateArgumentHolder;
import templates.expression_enum_template_argument.EnumTemplateArgument_Color;

public class ExpressionEnumTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final EnumTemplateArgument_Color enumTemplateArgument_Color = new EnumTemplateArgument_Color(false, 10);
        assertTrue(enumTemplateArgument_Color.hasExpressionField());

        final EnumTemplateArgumentHolder enumTemplateArgumentHolder =
                new EnumTemplateArgumentHolder(enumTemplateArgument_Color);
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        enumTemplateArgumentHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final EnumTemplateArgumentHolder readEnumTemplateArgumentHolder = new EnumTemplateArgumentHolder(reader);
        reader.close();
        assertTrue(enumTemplateArgumentHolder.equals(readEnumTemplateArgumentHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
