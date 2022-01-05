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

import templates.struct_template_clash_across_packages.pkg1.InstantiationInPkg1;
import templates.struct_template_clash_across_packages.pkg2.InstantiationInPkg2;
import templates.struct_template_clash_across_packages.test_struct.TestStruct_Test_67B82BA5;
import templates.struct_template_clash_across_packages.test_struct.TestStruct_Test_639610D0;

public class StructTemplateClashAcrossPackagesTest
{
    @Test
    public void readWriteInPkg1() throws IOException
    {
        final InstantiationInPkg1 instantiationInPkg1 = new InstantiationInPkg1();
        instantiationInPkg1.setTest(new TestStruct_Test_639610D0(
                new templates.struct_template_clash_across_packages.pkg1.Test(42)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiationInPkg1.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiationInPkg1 readInstantiationInPkg1 = new InstantiationInPkg1(reader);
        reader.close();
        assertTrue(instantiationInPkg1.equals(readInstantiationInPkg1));
    }

    @Test
    public void readWriteInPkg2() throws IOException
    {
        final InstantiationInPkg2 instantiationInPkg2 = new InstantiationInPkg2();
        instantiationInPkg2.setTest(new TestStruct_Test_67B82BA5(
                new templates.struct_template_clash_across_packages.pkg2.Test("string")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiationInPkg2.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiationInPkg2 readInstantiationInPkg2 = new InstantiationInPkg2(reader);
        reader.close();
        assertTrue(instantiationInPkg2.equals(readInstantiationInPkg2));
    }

    private static final File TEST_FILE = new File("test.bin");
}
