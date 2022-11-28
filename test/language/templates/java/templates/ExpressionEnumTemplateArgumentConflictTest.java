package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.expression_enum_template_argument_conflict.EnumTemplateArgumentConflictHolder;
import templates.expression_enum_template_argument_conflict.EnumTemplateArgumentConflict_Letters;

public class ExpressionEnumTemplateArgumentConflictTest
{
    @Test
    public void readWrite() throws IOException
    {
        final EnumTemplateArgumentConflict_Letters enumTemplateArgumentConflict_Letters =
                new EnumTemplateArgumentConflict_Letters(false, 10);
        assertTrue(enumTemplateArgumentConflict_Letters.isExpressionFieldUsed());

        final EnumTemplateArgumentConflictHolder enumTemplateArgumentConflictHolder =
                new EnumTemplateArgumentConflictHolder(enumTemplateArgumentConflict_Letters);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        enumTemplateArgumentConflictHolder.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final EnumTemplateArgumentConflictHolder readEnumTemplateArgumentConflictHolder =
                new EnumTemplateArgumentConflictHolder(reader);
        assertTrue(enumTemplateArgumentConflictHolder.equals(readEnumTemplateArgumentConflictHolder));
    }
}
