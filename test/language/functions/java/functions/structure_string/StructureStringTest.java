package functions.structure_string;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureStringTest
{
    @Test
    public void getPoolConst()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setPool(new StringPool());
        assertEquals("POOL_CONST", testStructure.funcGetPoolConst());
    }

    @Test
    public void getPoolField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setPool(new StringPool());
        assertEquals("POOL_FIELD", testStructure.funcGetPoolField());
    }

    @Test
    public void getConst()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setPool(new StringPool());
        assertEquals("CONST", testStructure.funcGetConst());
    }

    @Test
    public void getField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setPool(new StringPool());
        assertEquals("FIELD", testStructure.funcGetField());
    }
}
