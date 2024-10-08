package identifiers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import identifiers.bitmask_name_clashing_with_java.BitmaskNameClashingWithJava;
import identifiers.bitmask_name_clashing_with_java.String;

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
