package identifiers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import identifiers.bitmask_name_clashing_with_java.BitmaskNameClashingWithJava;
import identifiers.bitmask_name_clashing_with_java.String;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class BitmaskNameClashingWithJavaTest
{
    @Test
    public void emptyConstructor()
    {
        final BitmaskNameClashingWithJava bitmaskNameClashingWithJava =
                new BitmaskNameClashingWithJava(new String());
        assertEquals(0, bitmaskNameClashingWithJava.getStringField().getValue());
    }

    @Test
    public void bitSizeOf()
    {
        final BitmaskNameClashingWithJava bitmaskNameClashingWithJava =
                new BitmaskNameClashingWithJava(String.Values.WRITE);
        assertEquals(BIT_SIZE, bitmaskNameClashingWithJava.bitSizeOf());
    }

    @Test
    public void toStringMethod()
    {
        final BitmaskNameClashingWithJava bitmaskNameClashingWithJava =
                new BitmaskNameClashingWithJava(String.Values.READ);
        assertEquals("1[READ]", bitmaskNameClashingWithJava.getStringField().toString());
    }

    private static final int BIT_SIZE = 8;
}
