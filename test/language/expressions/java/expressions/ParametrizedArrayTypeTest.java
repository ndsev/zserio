package expressions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zserio.runtime.array.ObjectArray;

import expressions.parametrized_array_type.ParametrizedArrayElement;
import expressions.parametrized_array_type.ParametrizedArrayHolder;
import expressions.parametrized_array_type.ParametrizedArrayTypeExpression;

public class ParametrizedArrayTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final List<ParametrizedArrayElement> array = new ArrayList<ParametrizedArrayElement>();
        array.add(new ParametrizedArrayElement(false, 0, null));
        array.add(new ParametrizedArrayElement(false, 0, null));
        final ParametrizedArrayHolder parametrizedArrayHolder =
                new ParametrizedArrayHolder(false, new ObjectArray<ParametrizedArrayElement>(array));
        final ParametrizedArrayTypeExpression parametrizedArrayTypeExpression =
                new ParametrizedArrayTypeExpression(parametrizedArrayHolder, true);

        assertEquals(PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                parametrizedArrayTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final List<ParametrizedArrayElement> array = new ArrayList<ParametrizedArrayElement>();
        array.add(new ParametrizedArrayElement(false, 1, null));
        array.add(new ParametrizedArrayElement(false, 1, null));
        final ParametrizedArrayHolder parametrizedArrayHolder =
                new ParametrizedArrayHolder(false, new ObjectArray<ParametrizedArrayElement>(array));
        final ParametrizedArrayTypeExpression parametrizedArrayTypeExpression =
                new ParametrizedArrayTypeExpression(parametrizedArrayHolder, false);

        assertEquals(PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                parametrizedArrayTypeExpression.bitSizeOf());
    }

    private static final int PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 33;
    private static final int PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 32;
}
