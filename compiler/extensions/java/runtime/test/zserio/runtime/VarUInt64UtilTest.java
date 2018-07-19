package zserio.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VarUInt64UtilTest
{
    @Test
    public void convertVarUInt64ToInt_convertZero()
    {
        assertEquals(0, VarUInt64Util.convertVarUInt64ToInt(0));
    }

    @Test
    public void convertVarUInt64ToInt_convertIntMax()
    {
        assertEquals(Integer.MAX_VALUE, VarUInt64Util.convertVarUInt64ToInt(Integer.MAX_VALUE));
    }

    @Test(expected=RuntimeException.class)
    public void convertVarUInt64ToInt_convertGEIntMax()
    {
        VarUInt64Util.convertVarUInt64ToInt((long) Integer.MAX_VALUE + 1);
    }

    @Test(expected=RuntimeException.class)
    public void convertVarUInt64ToInt_convertLongMax()
    {
        VarUInt64Util.convertVarUInt64ToInt(Long.MAX_VALUE);
    }

    @Test
    public void convertVarUInt64ToArraySize_convertZero()
    {
        assertEquals(0, VarUInt64Util.convertVarUInt64ToArraySize(0));
    }

    @Test
    public void convertVarUInt64ToArraySize_convertIntMax()
    {
        assertEquals(Integer.MAX_VALUE, VarUInt64Util.convertVarUInt64ToArraySize(Integer.MAX_VALUE));
    }

    @Test(expected=RuntimeException.class)
    public void convertVarUInt64ToArraySize_convertGEIntMax()
    {
        VarUInt64Util.convertVarUInt64ToArraySize((long) Integer.MAX_VALUE + 1);
    }

    @Test(expected=RuntimeException.class)
    public void convertVarUInt64ToArraySize_convertLongMax()
    {
        VarUInt64Util.convertVarUInt64ToArraySize(Long.MAX_VALUE);
    }
}
