package literals;

import static org.junit.Assert.*;

import org.junit.Test;

public class LiteralsTest
{
    @Test
    public void booleanType()
    {
        assertEquals(true, __ConstType.BOOLEAN_TRUE);
        assertEquals(false, __ConstType.BOOLEAN_FALSE);
    }

    @Test
    public void decimal()
    {
        assertEquals((int)(255), __ConstType.DECIMAL_POSITIVE);
        assertEquals((int)(255), __ConstType.DECIMAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), __ConstType.DECIMAL_NEGATIVE);
    }

    @Test
    public void hexadecimal()
    {
        assertEquals((int)(255), __ConstType.HEXADECIMAL_POSITIVE);
        assertEquals((int)(255), __ConstType.HEXADECIMAL_POSITIVE_WITH_CAPITAL_X);
        assertEquals((int)(255), __ConstType.HEXADECIMAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), __ConstType.HEXADECIMAL_NEGATIVE);
    }

    @Test
    public void octal()
    {
        assertEquals((int)(255), __ConstType.OCTAL_POSITIVE);
        assertEquals((int)(255), __ConstType.OCTAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), __ConstType.OCTAL_NEGATIVE);
    }

    @Test
    public void binary()
    {
        assertEquals((int)(255), __ConstType.BINARY_POSITIVE);
        assertEquals((int)(255), __ConstType.BINARY_POSITIVE_WITH_CAPITAL_B);
        assertEquals((int)(255), __ConstType.BINARY_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), __ConstType.BINARY_NEGATIVE);
    }

    @Test
    public void float16Type()
    {
        assertEquals(15.2f, __ConstType.FLOAT16, 0.00001f);
    }

    @Test
    public void float32Type()
    {
        assertEquals(15.23f, __ConstType.FLOAT32, 0.00001f);
    }

    @Test
    public void float64Type()
    {
        assertEquals(15.234, __ConstType.FLOAT64, 0.000000001);
    }

    @Test
    public void string()
    {
        assertTrue(__ConstType.STRING.equals("String"));
    }
}
