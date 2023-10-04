package optional_members;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import optional_members.optional_expression_with_removed_enum_item.Compound;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class OptionalExpressionWithRemovedEnumItemTest
{
    @Test
    public void writeRead()
    {
        final Compound compound = new Compound(12, new long[] {1, 2});
        final BitBuffer bitBuffer = SerializeUtil.serialize(compound);

        final Compound readCompound = SerializeUtil.deserialize(Compound.class, bitBuffer);
        assertEquals(compound, readCompound);
    }
}
