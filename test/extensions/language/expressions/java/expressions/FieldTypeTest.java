package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.field_type.ContainedType;
import expressions.field_type.FieldTypeExpression;

public class FieldTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final ContainedType containedType = new ContainedType(true);
        final FieldTypeExpression fieldTypeExpression = new FieldTypeExpression(containedType, EXTRA_VALUE);

        assertEquals(COMPOUND_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, fieldTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final ContainedType containedType = new ContainedType(false);
        final FieldTypeExpression fieldTypeExpression = new FieldTypeExpression();
        fieldTypeExpression.setContainedType(containedType);

        assertEquals(COMPOUND_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, fieldTypeExpression.bitSizeOf());
    }

    private static final int COMPOUND_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 4;
    private static final int COMPOUND_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 1;

    private static final byte EXTRA_VALUE = 0x02;
}
