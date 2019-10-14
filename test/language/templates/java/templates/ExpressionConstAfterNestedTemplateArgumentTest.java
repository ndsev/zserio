package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.expression_const_after_nested_template_argument.ConstAfterNested;
import templates.expression_const_after_nested_template_argument.Compound_Element_uint32_SIZE;
import templates.expression_const_after_nested_template_argument.Element_uint32;

public class ExpressionConstAfterNestedTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        ObjectArray<Element_uint32> array = new ObjectArray<Element_uint32>(3);
        array.setElementAt(new Element_uint32(1), 0);
        array.setElementAt(new Element_uint32(2), 1);
        array.setElementAt(new Element_uint32(3), 2);

        final ConstAfterNested constAfterNested = new ConstAfterNested(new Compound_Element_uint32_SIZE(array));
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        constAfterNested.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final ConstAfterNested readConstAfterNested =
                new ConstAfterNested(reader);
        reader.close();
        assertTrue(constAfterNested.equals(readConstAfterNested));
    }

    private static final File TEST_FILE = new File("test.bin");
}
