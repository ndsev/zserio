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

import templates.struct_template_clash_other_type.InstantiationNameClashOtherType;
import templates.struct_template_clash_other_type.Test_uint32_99604043;

public class StructTemplateClashOtherTypeTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiationNameClashOtherType instantiationNameClashOtherType =
                new InstantiationNameClashOtherType(new Test_uint32_99604043(42));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiationNameClashOtherType.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiationNameClashOtherType readInstantiationNameClashOtherType =
                new InstantiationNameClashOtherType(reader);
        reader.close();
        assertTrue(instantiationNameClashOtherType.equals(readInstantiationNameClashOtherType));
    }

    private static final File TEST_FILE = new File("test.bin");
}
