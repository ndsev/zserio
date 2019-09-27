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

import templates.expression_full_enum_template_argument.FullEnumTemplateArgumentHolder;
import templates.expression_full_enum_template_argument.FullEnumTemplateArgument_Color;
import templates.expression_full_enum_template_argument.
        FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color;

public class ExpressionFullEnumTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final FullEnumTemplateArgument_Color colorInternal =
                new FullEnumTemplateArgument_Color(false, 10);
        final FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color
                colorExternal =
                new FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color(
                        false, 10);
        final FullEnumTemplateArgumentHolder fullEnumTemplateArgumentHolder =
                new FullEnumTemplateArgumentHolder(colorInternal, colorExternal);
        assertTrue(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentInternal().hasExpressionField());
        assertFalse(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentExternal().hasExpressionField());

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        fullEnumTemplateArgumentHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final FullEnumTemplateArgumentHolder readFullEnumTemplateArgumentHolder =
                new FullEnumTemplateArgumentHolder(reader);
        reader.close();
        assertTrue(fullEnumTemplateArgumentHolder.equals(readFullEnumTemplateArgumentHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
