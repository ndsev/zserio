package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.function_templated_return_type.FunctionTemplatedReturnType;
import templates.function_templated_return_type.TestStructure_uint32;
import templates.function_templated_return_type.TestStructure_string;
import templates.function_templated_return_type.TestStructure_float32;
import templates.function_templated_return_type.Holder_uint32;
import templates.function_templated_return_type.Holder_string;


public class FunctionTemplatedReturnTypeTest
{
    @Test
    public void readWrite() throws IOException
    {
        final FunctionTemplatedReturnType functionTemplatedReturnType = new FunctionTemplatedReturnType();
        final boolean hasHolder = true;
        functionTemplatedReturnType.setHasHolder(hasHolder);
        final TestStructure_uint32 uint32Test = new TestStructure_uint32(hasHolder);
        uint32Test.setHolder(new Holder_uint32(42));
        functionTemplatedReturnType.setUint32Test(uint32Test);
        final TestStructure_string stringTest = new TestStructure_string(hasHolder);
        stringTest.setHolder(new Holder_string("string"));
        functionTemplatedReturnType.setStringTest(stringTest);
        final TestStructure_float32 floatTest = new TestStructure_float32(false);
        floatTest.setValue(4.2f);
        functionTemplatedReturnType.setFloatTest(floatTest);

        assertEquals(42, functionTemplatedReturnType.getUint32Test().funcGet());
        assertEquals("string", functionTemplatedReturnType.getStringTest().funcGet());
        assertEquals(4.2f, functionTemplatedReturnType.getFloatTest().funcGet(), 0.001f);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        functionTemplatedReturnType.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final FunctionTemplatedReturnType readFunctionTemplatedReturnType =
                new FunctionTemplatedReturnType(reader);

        assertTrue(functionTemplatedReturnType.equals(readFunctionTemplatedReturnType));
    }
}
