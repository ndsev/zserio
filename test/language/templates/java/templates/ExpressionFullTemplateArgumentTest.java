package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.expression_full_template_argument.FullTemplateArgumentHolder;
import templates.expression_full_template_argument.FullTemplateArgument_Color_7C6F461F;
import templates.expression_full_template_argument.FullTemplateArgument_Color_F30EBCB3;

public class ExpressionFullTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final FullTemplateArgument_Color_7C6F461F colorInternal =
                new FullTemplateArgument_Color_7C6F461F(false, 10);
        assertTrue(colorInternal.isExpressionFieldUsed());

        final FullTemplateArgument_Color_F30EBCB3 colorExternal =
                new FullTemplateArgument_Color_F30EBCB3(false, 10);
        assertFalse(colorExternal.isExpressionFieldUsed());

        final FullTemplateArgumentHolder fullTemplateArgumentHolder =
                new FullTemplateArgumentHolder(colorInternal, colorExternal);
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
