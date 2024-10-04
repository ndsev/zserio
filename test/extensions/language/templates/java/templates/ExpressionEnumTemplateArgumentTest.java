package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.expression_enum_template_argument.EnumTemplateArgumentHolder;
import templates.expression_enum_template_argument.EnumTemplateArgument_Color;

public class ExpressionEnumTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final EnumTemplateArgument_Color enumTemplateArgument_Color = new EnumTemplateArgument_Color(false, 10);
        assertTrue(enumTemplateArgument_Color.isExpressionFieldUsed());

        final EnumTemplateArgumentHolder enumTemplateArgumentHolder =
                new EnumTemplateArgumentHolder(enumTemplateArgument_Color);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        enumTemplateArgumentHolder.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final EnumTemplateArgumentHolder readEnumTemplateArgumentHolder =
                new EnumTemplateArgumentHolder(reader);
        assertTrue(enumTemplateArgumentHolder.equals(readEnumTemplateArgumentHolder));
    }
}
