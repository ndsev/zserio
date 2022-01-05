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

import templates.struct_template_clash_other_template.InstantiationNameClashOtherTemplate;
import templates.struct_template_clash_other_template.A_uint32;
import templates.struct_template_clash_other_template.Test_A_uint32_5D68B0C2;
import templates.struct_template_clash_other_template.Test_A_uint32_FA82A3B7;

public class StructTemplateClashOtherTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiationNameClashOtherTemplate instantiationNameClashOtherTemplate =
                new InstantiationNameClashOtherTemplate(
                        new Test_A_uint32_FA82A3B7(42),
                        new Test_A_uint32_5D68B0C2(new A_uint32(10)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiationNameClashOtherTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiationNameClashOtherTemplate readInstantiationNameClashOtherTemplate =
                new InstantiationNameClashOtherTemplate(reader);
        reader.close();
        assertTrue(instantiationNameClashOtherTemplate.equals(readInstantiationNameClashOtherTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
