package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.templated_struct_recursion.TemplatedStructRecursion;
import templates.templated_struct_recursion.RecursiveTemplate_uint32;

public class TemplatedStructRecursionTest
{
    @Test
    public void writeRead() throws IOException
    {
        final TemplatedStructRecursion templatedStructRecursion = new TemplatedStructRecursion(
                new RecursiveTemplate_uint32(
                    new long[] {1, 2, 3},
                    new RecursiveTemplate_uint32(
                            new long[] {2, 3, 4},
                            new RecursiveTemplate_uint32(
                                new long[] {},
                                null
                            )
                    )
                )
        );

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        templatedStructRecursion.write(writer);
        writer.close();
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());

        final TemplatedStructRecursion readTemplatedStructRecursion = new TemplatedStructRecursion(reader);
        reader.close();
        assertTrue(templatedStructRecursion.equals(readTemplatedStructRecursion));
    }
}
