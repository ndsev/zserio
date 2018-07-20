package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.used_before_type.Color;
import expressions.used_before_type.UsedBeforeTypeExpression;

public class UsedBeforeTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final UsedBeforeTypeExpression usedBeforeTypeExpression = new UsedBeforeTypeExpression(Color.RED, true);

        assertEquals(USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, usedBeforeTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final UsedBeforeTypeExpression usedBeforeTypeExpression = new UsedBeforeTypeExpression();
        usedBeforeTypeExpression.setColor(Color.BLUE);

        assertEquals(USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                usedBeforeTypeExpression.bitSizeOf());
    }

    private static final int USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8;
    private static final int USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7;
}
