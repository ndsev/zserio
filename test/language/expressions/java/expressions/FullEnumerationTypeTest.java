package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.full_enumeration_type.Color;
import expressions.full_enumeration_type.FullEnumerationTypeExpression;

public class FullEnumerationTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final FullEnumerationTypeExpression fullEnumerationTypeExpression =
                new FullEnumerationTypeExpression(Color.RED, true);

        assertEquals(FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                fullEnumerationTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final FullEnumerationTypeExpression fullEnumerationTypeExpression = new FullEnumerationTypeExpression();
        fullEnumerationTypeExpression.setColor(Color.BLUE);

        assertEquals(FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                fullEnumerationTypeExpression.bitSizeOf());
    }

    private static final int FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9;
    private static final int FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8;
}
