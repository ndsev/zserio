package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        fullTemplateArgumentHolder.write(writer);
        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());

        final FullTemplateArgumentHolder readFullTemplateArgumentHolder =
                new FullTemplateArgumentHolder(reader);
        assertTrue(fullTemplateArgumentHolder.equals(readFullTemplateArgumentHolder));
    }
}
