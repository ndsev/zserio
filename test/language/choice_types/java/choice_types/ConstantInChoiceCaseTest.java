package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

import choice_types.constant_in_choice_case.ConstantInChoiceCase;
import choice_types.constant_in_choice_case.UINT8_CONST;

public class ConstantInChoiceCaseTest
{
    @Test
    public void writeRead()
    {
        final ConstantInChoiceCase constantInChoiceCase = new ConstantInChoiceCase(UINT8_CONST.UINT8_CONST);
        constantInChoiceCase.setConstCase((short)42);

        final BitBuffer bitBuffer = SerializeUtil.serialize(constantInChoiceCase);

        final ConstantInChoiceCase readConstantInChoiceCase =
                SerializeUtil.deserialize(ConstantInChoiceCase.class, bitBuffer, UINT8_CONST.UINT8_CONST);
        assertEquals(constantInChoiceCase, readConstantInChoiceCase);
    }
}
