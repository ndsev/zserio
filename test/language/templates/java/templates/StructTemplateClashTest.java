package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.struct_template_clash.InstantiationNameClash;
import templates.struct_template_clash.TestStruct_uint32;
import templates.struct_template_clash.Template_A_B_C_7FE93D34;
import templates.struct_template_clash.Template_A_B_C_5EB4E3FC;
import templates.struct_template_clash.A_B;
import templates.struct_template_clash.C;
import templates.struct_template_clash.A;
import templates.struct_template_clash.B_C;

public class StructTemplateClashTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TestStruct_uint32 testStruct_uint32 = new TestStruct_uint32(
                42,
                new Template_A_B_C_7FE93D34(new A_B(1), new C(true)),
                new Template_A_B_C_5EB4E3FC(new A(1), new B_C("string")));
        final InstantiationNameClash instantiationNameClash = new InstantiationNameClash(testStruct_uint32);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiationNameClash.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiationNameClash readInstantiationNameClash = new InstantiationNameClash(reader);
        reader.close();
        assertTrue(instantiationNameClash.equals(readInstantiationNameClash));
    }

    private static final File TEST_FILE = new File("test.bin");
}
