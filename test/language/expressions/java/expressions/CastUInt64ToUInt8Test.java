package expressions;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import expressions.cast_uint64_to_uint8.CastUInt64ToUInt8Expression;

public class CastUInt64ToUInt8Test
{
    @Test
    public void uint8ValueUsingUInt64Value()
    {
        final BigInteger uint64Value = new BigInteger("FFFFFFFFFFFFFFFE", 16);
        final CastUInt64ToUInt8Expression castUInt64ToUInt8Expression =
                new CastUInt64ToUInt8Expression(uint64Value, false);
        final short expectedUInt8Value = uint64Value.shortValue();
        assertEquals(expectedUInt8Value, castUInt64ToUInt8Expression.uint8Value());
    }

    @Test
    public void uint8ValueUsingConstant()
    {
        final BigInteger uint64Value = new BigInteger("FFFFFFFFFFFFFFFE", 16);
        final CastUInt64ToUInt8Expression castUInt64ToUInt8Expression =
                new CastUInt64ToUInt8Expression(uint64Value, true);
        final short expectedUInt8Value = 1;
        assertEquals(expectedUInt8Value, castUInt64ToUInt8Expression.uint8Value());
    }
}
