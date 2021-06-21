import unittest
import enum

from zserio.typeinfo import TypeInfo, MemberInfo, CaseInfo, ItemInfo, TypeAttribute, MemberAttribute

class TypeInfoTest(unittest.TestCase):
    class DummyType:
        pass

    class DummyEnum(enum.Enum):
        ONE = 1
        TWO = 2

    def test_type_info(self):
        type_info_1 = TypeInfo("schema_name", TypeInfoTest.DummyType)
        self.assertEqual("schema_name", type_info_1.schema_name)
        self.assertEqual(TypeInfoTest.DummyType, type_info_1.py_type)
        self.assertEqual(0, len(type_info_1.attributes))

        type_info_2 = TypeInfo("schema_name", TypeInfoTest.DummyType,
            attributes={TypeAttribute.FIELDS : [
                MemberInfo("member_1", type_info_1),
                MemberInfo("member_2", type_info_1,
                           attributes={MemberAttribute.OPTIONAL : None,
                                       MemberAttribute.PROPERTY_NAME : "member_2_prop_name"})
            ]}
        )

        self.assertEqual("schema_name", type_info_2.schema_name)
        self.assertEqual(TypeInfoTest.DummyType, type_info_2.py_type)
        self.assertEqual(1, len(type_info_2.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info_2.attributes)

        fields = type_info_2.attributes[TypeAttribute.FIELDS]
        member_1 = fields[0]
        self.assertEqual("member_1", member_1.schema_name)
        self.assertEqual(type_info_1, member_1.type_info)
        self.assertEqual(0, len(member_1.attributes))
        member_2 = fields[1]
        self.assertEqual("member_2", member_2.schema_name)
        self.assertEqual(type_info_1, member_2.type_info)
        self.assertEqual(2, len(member_2.attributes))
        self.assertIn(MemberAttribute.OPTIONAL, member_2.attributes)
        self.assertIsNone(member_2.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.PROPERTY_NAME, member_2.attributes)
        self.assertEqual("member_2_prop_name", member_2.attributes[MemberAttribute.PROPERTY_NAME])

    def test_member_info(self):
        member_info = MemberInfo("schema_name", TypeInfo("uint32", int), attributes={
            MemberAttribute.PROPERTY_NAME: "property_name"
        })
        self.assertEqual("schema_name", member_info.schema_name)
        self.assertEqual("uint32", member_info.type_info.schema_name)
        self.assertEqual(int, member_info.type_info.py_type)
        self.assertEqual(1, len(member_info.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, member_info.attributes)
        self.assertEqual("property_name", member_info.attributes[MemberAttribute.PROPERTY_NAME])

        member_info = MemberInfo("schema_name", TypeInfo("uint32", int), attributes={
            MemberAttribute.ALIGN: "8"
        })
        self.assertEqual(1, len(member_info.attributes))
        self.assertEqual("8", member_info.attributes[MemberAttribute.ALIGN])

    def test_case_info(self):
        member_info = MemberInfo("schema_name", TypeInfo("uint32", int))
        case_info = CaseInfo(["1", "2"], member_info)
        self.assertEqual(["1", "2"], case_info.case_expressions)
        self.assertEqual(member_info, case_info.field)

        case_info = CaseInfo([], None) # default without field
        self.assertEqual(0, len(case_info.case_expressions))
        self.assertIsNone(case_info.field)

    def test_item_info(self):
        item_info = ItemInfo("one", TypeInfoTest.DummyEnum.ONE)
        self.assertEqual("one", item_info.schema_name)
        self.assertEqual(TypeInfoTest.DummyEnum.ONE, item_info.py_item)
