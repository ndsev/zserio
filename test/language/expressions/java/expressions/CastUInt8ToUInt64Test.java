package expressions;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import expressions.cast_uint8_to_uint64.CastUInt8ToUInt64Expression;

public class CastUInt8ToUInt64Test
{
    @Test
    public void uint64ValueUsingUInt8Value()
    {
        final short uint8Value = 0xBA;
        final CastUInt8ToUInt64Expression castUInt8ToUInt64Expression =
                new CastUInt8ToUInt64Expression(uint8Value, false);
        final BigInteger expectedUInt64Value = BigInteger.valueOf(uint8Value);
        assertEquals(expectedUInt64Value, castUInt8ToUInt64Expression.funcUint64Value());
    }

    @Test
    public void uint64ValueUsingConstant()
    {
        final short uint8Value = 0xBA;
        final CastUInt8ToUInt64Expression castUInt8ToUInt64Expression =
                new CastUInt8ToUInt64Expression(uint8Value, true);
        final BigInteger expectedUInt64Value = BigInteger.ONE;
        assertEquals(expectedUInt64Value, castUInt8ToUInt64Expression.funcUint64Value());
    }
}
