package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import allow_implicit_arrays.lengthof_with_implicit_array.LengthOfWithImplicitArray;

public class LengthOfWithImplicitArrayTest
{
    @Test
    public void getLengthOfImplicitArray()
    {
        final LengthOfWithImplicitArray lengthOfWithImplicitArray = new LengthOfWithImplicitArray();
        final int implicitArrayLength = 12;
        final short[] implicitArray = new short[implicitArrayLength];
        lengthOfWithImplicitArray.setImplicitArray(implicitArray);
        assertEquals(implicitArrayLength, lengthOfWithImplicitArray.funcGetLengthOfImplicitArray());
    }
}
