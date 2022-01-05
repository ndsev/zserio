package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class VarSizeUtilTest
{
    @Test
    public void convertBitBufferSizeToInt()
    {
        assertEquals(0, VarSizeUtil.convertBitBufferSizeToInt(0));
        assertEquals(Integer.MAX_VALUE, VarSizeUtil.convertBitBufferSizeToInt(Integer.MAX_VALUE));
    }

    @Test
    public void convertBitBufferSizeToIntMinOverflow()
    {
        assertThrows(RuntimeException.class,
                () -> VarSizeUtil.convertBitBufferSizeToInt((long)Integer.MAX_VALUE + 1));
    }

    @Test
    public void convertBitBufferSizeToIntMaxOverflow()
    {
        assertThrows(RuntimeException.class, () -> VarSizeUtil.convertBitBufferSizeToInt(Long.MAX_VALUE));
    }
}
