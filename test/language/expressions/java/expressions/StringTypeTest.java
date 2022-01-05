package expressions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import expressions.string_type.StringTypeExpression;
import expressions.string_type.STRING_CONSTANT;

public class StringTypeTest
{
    @Test
    public void append()
    {
        final StringTypeExpression stringTypeExpression = new StringTypeExpression(VALUE);
        assertEquals(VALUE, stringTypeExpression.funcReturnValue());
        assertEquals("appendix", stringTypeExpression.funcAppendix());
        assertEquals(STRING_CONSTANT.STRING_CONSTANT + "_appendix", stringTypeExpression.funcAppendToConst());
    }

    private static final String VALUE = "value";
}
