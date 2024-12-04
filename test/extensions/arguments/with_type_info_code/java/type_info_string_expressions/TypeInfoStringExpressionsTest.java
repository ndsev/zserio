package type_info_string_expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;
import zserio.runtime.typeinfo.CaseInfo;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;

public class TypeInfoStringExpressionsTest
{
    @Test
    public void typeInfo()
    {
        final TypeInfoStringExpressions typeInfoStringExpressions = createData();
        final TypeInfo typeInfo = TypeInfoStringExpressions.typeInfo();

        // choiceField
        final TestChoice choiceField = typeInfoStringExpressions.getChoiceField();
        final FieldInfo choiceFieldInfo = typeInfo.getFields().get(0);
        assertEquals((int)LENGTHOF_LITERAL,
                choiceFieldInfo.getTypeArguments().get(0).apply(typeInfoStringExpressions, 0));

        // ChoiceType
        final TypeInfo choiceTypeInfo = choiceFieldInfo.getTypeInfo();
        final String choiceSelector = choiceTypeInfo.getSelector();
        assertEquals("getSelector() + zserio.runtime.BuiltInOperators.lengthOf(\"literal\")", choiceSelector);

        final CaseInfo caseInfo0 = choiceTypeInfo.getCases().get(0);
        assertEquals((int)LENGTHOF_LITERAL, caseInfo0.getCaseExpressions().get(0).get());

        // structField
        final TestStruct structField = choiceField.getStructField();
        final FieldInfo structFieldInfo = choiceTypeInfo.getFields().get(1);
        assertEquals((int)LENGTHOF_LITERAL, structFieldInfo.getTypeArguments().get(0).apply(choiceField, 0));

        // TestStruct
        final TypeInfo structTypeInfo = structFieldInfo.getTypeInfo();

        // arrayField
        final FieldInfo arrayFieldInfo = structTypeInfo.getFields().get(0);
        assertEquals(2 * LENGTHOF_LITERAL, arrayFieldInfo.getArrayLength().applyAsInt(structField));
        assertTrue(arrayFieldInfo.getConstraint().test(structField));

        // dynBitField
        final FieldInfo dynBitFieldInfo = structTypeInfo.getFields().get(1);
        assertEquals((int)LENGTHOF_LITERAL, dynBitFieldInfo.getTypeArguments().get(0).apply(structField, 0));
        assertTrue(dynBitFieldInfo.getOptionalCondition().test(structField));

        // bitField
        final FieldInfo bitFieldInfo = structTypeInfo.getFields().get(2);
        assertEquals((int)LENGTHOF_LITERAL + 1, bitFieldInfo.getAlignment().getAsInt());

        // enumField
        final FieldInfo enumFieldInfo = structTypeInfo.getFields().get(3);

        // TestEnum
        final TypeInfo enumTypeInfo = enumFieldInfo.getTypeInfo();
        assertEquals((int)LENGTHOF_LITERAL, enumTypeInfo.getUnderlyingTypeArguments().get(0).get());

        // bitmaskField
        final FieldInfo bitmaskFieldInfo = structTypeInfo.getFields().get(4);

        // TestBitmask
        final TypeInfo bitmaskTypeInfo = bitmaskFieldInfo.getTypeInfo();
        assertEquals((int)LENGTHOF_LITERAL, bitmaskTypeInfo.getUnderlyingTypeArguments().get(0).get());
    }

    @Test
    public void writeRead()
    {
        final TypeInfoStringExpressions typeInfoStringExpressions = createData();
        final BitBuffer bitBuffer = SerializeUtil.serialize(typeInfoStringExpressions);
        final TypeInfoStringExpressions readTypeInfoStringExpressions =
                SerializeUtil.deserialize(TypeInfoStringExpressions.class, bitBuffer);
        assertEquals(typeInfoStringExpressions, readTypeInfoStringExpressions);
    }

    private static TypeInfoStringExpressions createData()
    {
        final TestChoice testChoice = new TestChoice(LENGTHOF_LITERAL);
        testChoice.setStructField(new TestStruct(LENGTHOF_LITERAL,
                new long[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, (byte)0, (byte)0, TestEnum.ONE,
                TestBitmask.Values.READ));

        return new TypeInfoStringExpressions(testChoice);
    }

    private static final long LENGTHOF_LITERAL = 7;
}
