package expressions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import expressions.uint64_type.UInt64TypeExpression;

public class UInt64TypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final UInt64TypeExpression uint64TypeExpression = new UInt64TypeExpression(UINT32_VALUE,
                UINT64_VALUE_WITH_OPTIONAL, BOOLEAN_VALUE, ADDITIONAL_VALUE);

        assertEquals(UINT64_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, uint64TypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final UInt64TypeExpression uint64TypeExpression = new UInt64TypeExpression(UINT32_VALUE,
                UINT64_VALUE_WITHOUT_OPTIONAL, BOOLEAN_VALUE, null);

        assertEquals(UINT64_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, uint64TypeExpression.bitSizeOf());
    }

    private static final int UINT64_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 100;
    private static final int UINT64_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 97;

    private static final long UINT32_VALUE = 8;
    private static final BigInteger UINT64_VALUE_WITH_OPTIONAL = BigInteger.valueOf(2);
    private static final BigInteger UINT64_VALUE_WITHOUT_OPTIONAL = BigInteger.valueOf(1);
    private static final boolean BOOLEAN_VALUE = true;
    private static final byte ADDITIONAL_VALUE = 0x03;
}
