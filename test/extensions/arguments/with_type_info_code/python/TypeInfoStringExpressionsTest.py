import unittest
import zserio

from zserio.typeinfo import MemberAttribute, TypeAttribute

from testutils import getZserioApi


class TypeInfoStringExpressionsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "type_info_string_expressions.zs", extraArgs=["-withTypeInfoCode"])

    def testTypeInfo(self):
        typeInfoStringExpressions = self._createData()
        typeInfo = self.api.TypeInfoStringExpressions.type_info()

        # choiceField
        choiceField = typeInfoStringExpressions.choice_field
        choiceFieldInfo = typeInfo.attributes[TypeAttribute.FIELDS][0]
        self.assertEqual(
            self.LENGTHOF_LITERAL,
            choiceFieldInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0](typeInfoStringExpressions, 0),
        )

        # TestChoice
        choiceTypeInfo = choiceFieldInfo.type_info
        choiceSelector = choiceTypeInfo.attributes[TypeAttribute.SELECTOR]
        self.assertEqual(2 * self.LENGTHOF_LITERAL, choiceSelector(choiceField))

        caseInfo0 = choiceTypeInfo.attributes[TypeAttribute.CASES][0]
        self.assertEqual(self.LENGTHOF_LITERAL, caseInfo0.case_expressions[0]())

        # structField
        structField = choiceField.struct_field
        structFieldInfo = choiceTypeInfo.attributes[TypeAttribute.FIELDS][1]
        self.assertEqual(
            self.LENGTHOF_LITERAL, structFieldInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0](choiceField, 0)
        )

        # TestStruct
        structTypeInfo = structFieldInfo.type_info

        # arrayField
        arrayFieldInfo = structTypeInfo.attributes[TypeAttribute.FIELDS][0]
        self.assertEqual(
            2 * self.LENGTHOF_LITERAL, arrayFieldInfo.attributes[MemberAttribute.ARRAY_LENGTH](structField)
        )
        self.assertTrue(arrayFieldInfo.attributes[MemberAttribute.CONSTRAINT](structField))

        # dynBitField
        dynBitFieldInfo = structTypeInfo.attributes[TypeAttribute.FIELDS][1]
        self.assertEqual(
            self.LENGTHOF_LITERAL, dynBitFieldInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0](structField, 0)
        )
        self.assertTrue(dynBitFieldInfo.attributes[MemberAttribute.OPTIONAL](structField))

        # bitField
        bitFieldInfo = structTypeInfo.attributes[TypeAttribute.FIELDS][2]
        self.assertEqual(self.LENGTHOF_LITERAL + 1, bitFieldInfo.attributes[MemberAttribute.ALIGN]())

        # enumField
        enumFieldInfo = structTypeInfo.attributes[TypeAttribute.FIELDS][3]

        # TestEnum
        enumTypeInfo = enumFieldInfo.type_info
        self.assertEqual(
            self.LENGTHOF_LITERAL, enumTypeInfo.attributes[TypeAttribute.UNDERLYING_TYPE_ARGUMENTS][0]()
        )

        # bitmaskField
        bitmaskFieldInfo = structTypeInfo.attributes[TypeAttribute.FIELDS][4]

        # TestBitmask
        bitmaskTypeInfo = bitmaskFieldInfo.type_info
        self.assertEqual(
            self.LENGTHOF_LITERAL, bitmaskTypeInfo.attributes[TypeAttribute.UNDERLYING_TYPE_ARGUMENTS][0]()
        )

    def testWriteRead(self):
        typeInfoStringExpressions = self._createData()
        bitBuffer = zserio.serialize(typeInfoStringExpressions)
        readTypeInfoStringExpressions = zserio.deserialize(self.api.TypeInfoStringExpressions, bitBuffer)
        self.assertEqual(typeInfoStringExpressions, readTypeInfoStringExpressions)

    def _createData(self):
        return self.api.TypeInfoStringExpressions(
            self.api.TestChoice(
                self.LENGTHOF_LITERAL,
                struct_field_=self.api.TestStruct(
                    self.LENGTHOF_LITERAL,  # param
                    [0] * self.LENGTHOF_LITERAL * 2,  # array
                    0,  # dynBitField
                    0,  # bitField
                    self.api.TestEnum.ONE,
                    self.api.TestBitmask.Values.READ,
                ),
            )
        )

    LENGTHOF_LITERAL = 7
