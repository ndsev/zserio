package zserio.runtime.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class JsonDecoderTest
{
    @Test
    public void decodeNull()
    {
        checkDecoderSuccess("null", 0, 4, null);
        checkDecoderSuccess("{ } null", 4, 4, null);
        checkDecoderSuccess("null { }", 0, 4, null);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("invalid", 1, 4);
        checkDecoderFailure("nul", 0, 3);
    }

    @Test
    public void decodeTrue()
    {
        checkDecoderSuccess("true", 0, 4, true);
        checkDecoderSuccess("{ } true", 4, 4, true);
        checkDecoderSuccess("true { }", 0, 4, true);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("stainless", 1, 4);
        checkDecoderFailure("tru", 0, 3);
    }

    @Test
    public void decodeFalse()
    {
        checkDecoderSuccess("false", 0, 5, false);
        checkDecoderSuccess("{ } false", 4, 5, false);
        checkDecoderSuccess("false { }", 0, 5, false);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("affected", 1, 5);
        checkDecoderFailure("fal", 0, 3);
    }

    @Test
    public void decodeNan()
    {
        checkDecoderSuccess("NaN", 0, 3, Double.NaN);
        checkDecoderSuccess("{ } NaN", 4, 3, Double.NaN);
        checkDecoderSuccess("NaN { }", 0, 3, Double.NaN);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("iNactive", 1, 3);
        checkDecoderFailure("Na", 0, 2);
    }

    @Test
    public void decodePositiveInfinity()
    {
        checkDecoderSuccess("Infinity", 0, 8, Double.POSITIVE_INFINITY);
        checkDecoderSuccess("{ } Infinity", 4, 8, Double.POSITIVE_INFINITY);
        checkDecoderSuccess("Infinity { }", 0, 8, Double.POSITIVE_INFINITY);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("iInfinvalid", 1, 8);
        checkDecoderFailure("Infin", 0, 5);
    }

    @Test
    public void decodeNegativeInfinity()
    {
        checkDecoderSuccess("-Infinity", 0, 9, Double.NEGATIVE_INFINITY);
        checkDecoderSuccess("{ } -Infinity", 4, 9, Double.NEGATIVE_INFINITY);
        checkDecoderSuccess("-Infinity { }", 0, 9, Double.NEGATIVE_INFINITY);
        checkDecoderFailure("invalid", 0, 1);
        checkDecoderFailure("i-Infinvalid", 1, 9);
        checkDecoderFailure("-Infin", 0, 6);
        checkDecoderFailure("-Infix", 0, 6);
    }

    @Test
    public void decodeSignedIntegral()
    {
        checkDecoderSuccess("-0", 0, 2, BigInteger.ZERO);
        checkDecoderSuccess("{ } -0", 4, 2, BigInteger.ZERO);
        checkDecoderSuccess("-0 { }", 0, 2, BigInteger.ZERO);
        checkDecoderSuccess("-1", 0, 2, BigInteger.valueOf(-1));
        checkDecoderSuccess("-9223372036854775808", 0, 20, BigInteger.valueOf(Long.MIN_VALUE));

        checkDecoderFailure("--10", 0, 1);
    }

    @Test
    public void decodeUnsignedIntegral()
    {
        checkDecoderSuccess("0", 0, 1, BigInteger.ZERO);
        checkDecoderSuccess("{ } 0", 4, 1, BigInteger.ZERO);
        checkDecoderSuccess("0 { }", 0, 1, BigInteger.ZERO);
        checkDecoderSuccess("1", 0, 1, BigInteger.ONE);
        checkDecoderSuccess("9223372036854775807", 0, 19, BigInteger.valueOf(Long.MAX_VALUE));
        checkDecoderSuccess("18446744073709551615", 0, 20, new BigInteger("18446744073709551615"));

        checkDecoderFailure("+10", 0, 1);
    }

    @Test
    public void decodeDouble()
    {
        checkDecoderSuccess("0.0", 0, 3, 0.0);
        checkDecoderSuccess("{ } 0.0", 4, 3, 0.0);
        checkDecoderSuccess("0.0 { }", 0, 3, 0.0);
        checkDecoderSuccess("-1.0", 0, 4, -1.0);
        checkDecoderSuccess("1.0", 0, 3, 1.0);
        checkDecoderSuccess("3.5", 0, 3, 3.5);
        checkDecoderSuccess("9.875", 0, 5, 9.875);
        checkDecoderSuccess("0.6171875", 0, 9, 0.6171875);

        checkDecoderSuccess("1e+20", 0, 5, 1e+20);
        checkDecoderSuccess("1E+20", 0, 5, 1E+20);
        checkDecoderSuccess("1e-20", 0, 5, 1e-20);
        checkDecoderSuccess("1E-20", 0, 5, 1E-20);
        checkDecoderSuccess("-1e+20", 0, 6, -1e+20);
        checkDecoderSuccess("-1E+20", 0, 6, -1E+20);
        checkDecoderSuccess("-1e-20", 0, 6, -1e-20);
        checkDecoderSuccess("-1E-20", 0, 6, -1E-20);

        checkDecoderFailure("1EE20", 0, 2);
        checkDecoderFailure("1E++20", 0, 3);

        checkDecoderFailure("1e", 0, 2);
        checkDecoderFailure("1e+", 0, 3);
        checkDecoderFailure("1E-", 0, 3);
    }

    @Test
    public void decodeString()
    {
        checkDecoderSuccess("\"\"", 0, 2, "");
        checkDecoderSuccess("{ } \"\"", 4, 2, "");
        checkDecoderSuccess("\"\" { }", 0, 2, "");

        checkDecoderSuccess("\"test\"", 0, 6, "test");
        checkDecoderSuccess("\"München\"", 0, 9, "München");
        checkDecoderSuccess("\"€\"", 0, 3, "€");

        // escapes
        checkDecoderSuccess("\"\\\\\"", 0, 4, "\\");
        checkDecoderSuccess("\"\\\"\"", 0, 4, "\"");
        checkDecoderSuccess("\"\\b\"", 0, 4, "\b");
        checkDecoderSuccess("\"\\f\"", 0, 4, "\f");
        checkDecoderSuccess("\"\\n\"", 0, 4, "\n");
        checkDecoderSuccess("\"\\r\"", 0, 4, "\r");
        checkDecoderSuccess("\"\\t\"", 0, 4, "\t");

        checkDecoderSuccess("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"", 0, 62,
                "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2");

        // <= 0x1F -> unicode escape
        checkDecoderSuccess("\"\\u001f\"", 0, 8, "\u001f");

        // TODO[Mi-L@]: Fixme!
        //checkDecoderFailure("\"unterminated", 0, 13);
    }

    private void checkDecoderSuccess(String input, int pos, int expectedNumRead, Object expectedValue)
    {
        final JsonDecoder.Result result = JsonDecoder.decodeValue(input, pos);
        assertEquals(true, result.success());
        assertEquals(expectedValue, result.getValue());
        assertEquals(expectedNumRead, result.getNumReadChars());
    }

    private void checkDecoderFailure(String input, int pos, int expectedNumRead)
    {
        final JsonDecoder.Result result = JsonDecoder.decodeValue(input, pos);
        assertEquals(false, result.success());
        assertEquals(null, result.getValue());
        assertEquals(expectedNumRead, result.getNumReadChars());
    }
}
