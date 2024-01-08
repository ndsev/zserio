package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.enumeration_type.Color;
import expressions.enumeration_type.EnumerationTypeExpression;

public class EnumerationTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final EnumerationTypeExpression enumerationTypeExpression =
                new EnumerationTypeExpression(Color.RED, true);

        assertEquals(ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, enumerationTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final EnumerationTypeExpression enumerationTypeExpression = new EnumerationTypeExpression();
        enumerationTypeExpression.setColor(Color.BLUE);

        assertEquals(
                ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, enumerationTypeExpression.bitSizeOf());
    }

    private static final int ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9;
    private static final int ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8;
}
