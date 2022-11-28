package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.expression_bitmask_template_argument.BitmaskTemplateArgumentHolder;
import templates.expression_bitmask_template_argument.BitmaskTemplateArgument_Permission;

public class ExpressionBitmaskTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final BitmaskTemplateArgument_Permission bitmaskTemplateArgument_Permission =
                new BitmaskTemplateArgument_Permission(false, 10);
        assertTrue(bitmaskTemplateArgument_Permission.isExpressionFieldUsed());

        final BitmaskTemplateArgumentHolder bitmaskTemplateArgumentHolder =
                new BitmaskTemplateArgumentHolder(bitmaskTemplateArgument_Permission);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        bitmaskTemplateArgumentHolder.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final BitmaskTemplateArgumentHolder readBitmaskTemplateArgumentHolder =
                new BitmaskTemplateArgumentHolder(reader);
        assertTrue(bitmaskTemplateArgumentHolder.equals(readBitmaskTemplateArgumentHolder));
    }
}
