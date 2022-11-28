package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiationInPkg1.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiationInPkg1 readInstantiationInPkg1 = new InstantiationInPkg1(reader);
        assertTrue(instantiationInPkg1.equals(readInstantiationInPkg1));
    }

    @Test
    public void readWriteInPkg2() throws IOException
    {
        final InstantiationInPkg2 instantiationInPkg2 = new InstantiationInPkg2();
        instantiationInPkg2.setTest(new TestStruct_Test_67B82BA5(
                new templates.struct_template_clash_across_packages.pkg2.Test("string")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiationInPkg2.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiationInPkg2 readInstantiationInPkg2 = new InstantiationInPkg2(reader);
        assertTrue(instantiationInPkg2.equals(readInstantiationInPkg2));
    }
}
