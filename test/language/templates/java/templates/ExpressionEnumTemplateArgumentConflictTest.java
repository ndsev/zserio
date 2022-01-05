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
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        enumTemplateArgumentConflictHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final EnumTemplateArgumentConflictHolder readEnumTemplateArgumentConflictHolder =
                new EnumTemplateArgumentConflictHolder(reader);
        reader.close();
        assertTrue(enumTemplateArgumentConflictHolder.equals(readEnumTemplateArgumentConflictHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
