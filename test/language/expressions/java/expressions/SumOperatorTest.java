package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import zserio.runtime.array.UnsignedByteArray;

import expressions.sum_operator.SumFunction;

public class SumOperatorTest
{
    @Test
    public void getSumFixedArray()
    {
        final SumFunction sumFunction = new SumFunction();
        final short[] fixedArrayData = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final UnsignedByteArray fixedArray = new UnsignedByteArray(fixedArrayData, 0, fixedArrayData.length);
        sumFunction.setFixedArray(fixedArray);

        int expectedSum = 0;
        for (int element : fixedArrayData)
            expectedSum += element;
        assertEquals(expectedSum, sumFunction.funcGetSumFixedArray());
    }
}
