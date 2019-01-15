package expressions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zserio.runtime.array.ObjectArray;

import expressions.parameterized_array_type.ParameterizedArrayElement;
import expressions.parameterized_array_type.ParameterizedArrayHolder;
import expressions.parameterized_array_type.ParameterizedArrayTypeExpression;

public class ParameterizedArrayTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final List<ParameterizedArrayElement> array = new ArrayList<ParameterizedArrayElement>();
        array.add(new ParameterizedArrayElement(false, 0, null));
        array.add(new ParameterizedArrayElement(false, 0, null));
        final ParameterizedArrayHolder parameterizedArrayHolder =
                new ParameterizedArrayHolder(false, new ObjectArray<ParameterizedArrayElement>(array));
        final ParameterizedArrayTypeExpression parameterizedArrayTypeExpression =
                new ParameterizedArrayTypeExpression(parameterizedArrayHolder, true);

        assertEquals(PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                parameterizedArrayTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final List<ParameterizedArrayElement> array = new ArrayList<ParameterizedArrayElement>();
        array.add(new ParameterizedArrayElement(false, 1, null));
        array.add(new ParameterizedArrayElement(false, 1, null));
        final ParameterizedArrayHolder parameterizedArrayHolder =
                new ParameterizedArrayHolder(false, new ObjectArray<ParameterizedArrayElement>(array));
        final ParameterizedArrayTypeExpression parameterizedArrayTypeExpression =
                new ParameterizedArrayTypeExpression(parameterizedArrayHolder, false);

        assertEquals(PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                parameterizedArrayTypeExpression.bitSizeOf());
    }

    private static final int PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 33;
    private static final int PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 32;
}
