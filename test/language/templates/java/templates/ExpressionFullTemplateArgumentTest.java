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

import templates.expression_full_template_argument.FullTemplateArgumentHolder;
import templates.expression_full_template_argument.FullTemplateArgument_Color;
import templates.expression_full_template_argument.
        FullTemplateArgument_templates_expression_full_template_argument_color_Color;

public class ExpressionFullTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final FullTemplateArgument_Color colorInternal = new FullTemplateArgument_Color(false, 10);
        final FullTemplateArgument_templates_expression_full_template_argument_color_Color colorExternal =
                new FullTemplateArgument_templates_expression_full_template_argument_color_Color(false, 10);
        final FullTemplateArgumentHolder fullTemplateArgumentHolder =
                new FullTemplateArgumentHolder(colorInternal, colorExternal);
        assertTrue(fullTemplateArgumentHolder.getTemplateArgumentInternal().hasExpressionField());
        assertFalse(fullTemplateArgumentHolder.getTemplateArgumentExternal().hasExpressionField());

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        fullTemplateArgumentHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final FullTemplateArgumentHolder readFullTemplateArgumentHolder =
                new FullTemplateArgumentHolder(reader);
        reader.close();
        assertTrue(fullTemplateArgumentHolder.equals(readFullTemplateArgumentHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
