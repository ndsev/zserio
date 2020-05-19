package zserio.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VarSizeUtilTest
{
    @Test
    public void convertBitBufferSizeToInt()
    {
        assertEquals(0, VarSizeUtil.convertBitBufferSizeToInt(0));
        assertEquals(Integer.MAX_VALUE, VarSizeUtil.convertBitBufferSizeToInt(Integer.MAX_VALUE));
    }

    @Test(expected=RuntimeException.class)
    public void convertBitBufferSizeToIntMinOverflow()
    {
        VarSizeUtil.convertBitBufferSizeToInt((long)Integer.MAX_VALUE + 1);
    }

    @Test(expected=RuntimeException.class)
    public void convertBitBufferSizeToIntMaxOverflow()
    {
        VarSizeUtil.convertBitBufferSizeToInt(Long.MAX_VALUE);
    }
}
