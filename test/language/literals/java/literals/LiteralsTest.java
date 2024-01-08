package literals;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LiteralsTest
{
    @Test
    public void booleanType()
    {
        assertEquals(true, BOOLEAN_TRUE.BOOLEAN_TRUE);
        assertEquals(false, BOOLEAN_FALSE.BOOLEAN_FALSE);
    }

    @Test
    public void decimal()
    {
        assertEquals((int)(255), DECIMAL_POSITIVE.DECIMAL_POSITIVE);
        assertEquals((int)(255), DECIMAL_POSITIVE_WITH_SIGN.DECIMAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), DECIMAL_NEGATIVE.DECIMAL_NEGATIVE);
        assertEquals((int)(0), DECIMAL_ZERO.DECIMAL_ZERO);
    }

    @Test
    public void hexadecimal()
    {
        assertEquals((int)(255), HEXADECIMAL_POSITIVE.HEXADECIMAL_POSITIVE);
        assertEquals((int)(255), HEXADECIMAL_POSITIVE_WITH_CAPITAL_X.HEXADECIMAL_POSITIVE_WITH_CAPITAL_X);
        assertEquals((int)(255), HEXADECIMAL_POSITIVE_WITH_SIGN.HEXADECIMAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), HEXADECIMAL_NEGATIVE.HEXADECIMAL_NEGATIVE);
    }

    @Test
    public void octal()
    {
        assertEquals((int)(255), OCTAL_POSITIVE.OCTAL_POSITIVE);
        assertEquals((int)(255), OCTAL_POSITIVE_WITH_SIGN.OCTAL_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), OCTAL_NEGATIVE.OCTAL_NEGATIVE);
        assertEquals((int)(0), OCTAL_ZERO.OCTAL_ZERO);
    }

    @Test
    public void binary()
    {
        assertEquals((int)(255), BINARY_POSITIVE.BINARY_POSITIVE);
        assertEquals((int)(255), BINARY_POSITIVE_WITH_CAPITAL_B.BINARY_POSITIVE_WITH_CAPITAL_B);
        assertEquals((int)(255), BINARY_POSITIVE_WITH_SIGN.BINARY_POSITIVE_WITH_SIGN);
        assertEquals((int)(-255), BINARY_NEGATIVE.BINARY_NEGATIVE);
    }

    @Test
    public void float16Type()
    {
        assertEquals(15.2f, FLOAT16.FLOAT16, 0.00001f);
    }

    @Test
    public void float32Type()
    {
        assertEquals(15.23f, FLOAT32.FLOAT32, 0.00001f);
    }

    @Test
    public void float64Type()
    {
        assertEquals(15.234, FLOAT64.FLOAT64, 0.000000001);
    }

    @Test
    public void string()
    {
        assertTrue(STRING.STRING.equals("String with escaped values \u0031 \u0032 \063 \n \t \f \r \\ \""));
    }
}
