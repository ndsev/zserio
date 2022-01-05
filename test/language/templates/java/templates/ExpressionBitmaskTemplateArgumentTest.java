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
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        bitmaskTemplateArgumentHolder.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final BitmaskTemplateArgumentHolder readBitmaskTemplateArgumentHolder =
                new BitmaskTemplateArgumentHolder(reader);
        reader.close();
        assertTrue(bitmaskTemplateArgumentHolder.equals(readBitmaskTemplateArgumentHolder));
    }

    private static final File TEST_FILE = new File("test.bin");
}
