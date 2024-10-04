package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.function_type.Color;
import expressions.function_type.FunctionTypeExpression;

public class FunctionTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final FunctionTypeExpression functionTypeExpression = new FunctionTypeExpression(Color.RED, true);

        assertEquals(FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, functionTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final FunctionTypeExpression functionTypeExpression = new FunctionTypeExpression();
        functionTypeExpression.setColor(Color.BLUE);

        assertEquals(FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, functionTypeExpression.bitSizeOf());
    }

    private static final int FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9;
    private static final int FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8;
}
