import unittest
import os
import zserio

from zserio.typeinfo import TypeAttribute, MemberAttribute, TypeInfo

from WithTypeInfoCodeCreator import createWithTypeInfoCode

from testutils import getZserioApi, getApiDir

class WithTypeInfoCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_type_info_code.zs",
                               extraArgs=["-withTypeInfoCode", "-allowImplicitArrays"])

    def testSqlDatabase(self):
        self._checkSqlDatabase(self.api.SqlDatabase.type_info())

    def testSimplePubsub(self):
        self._checkSimplePubsub(self.api.SimplePubsub.type_info())

    def testSimpleService(self):
        self._checkSimpleService(self.api.SimpleService.type_info())

    def testWriteReadFileWithOptionals(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api, createOptionals=True)
        zserio.serialize_to_file(withTypeInfoCode, self.BLOB_NAME_WITH_OPTIONALS)

        readWithTypeInfoCode = zserio.deserialize_from_file(self.api.WithTypeInfoCode,
                                                            self.BLOB_NAME_WITH_OPTIONALS)
        self.assertEqual(withTypeInfoCode, readWithTypeInfoCode)

    def testWriteReadFileWithoutOptionals(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api, createOptionals=False)
        zserio.serialize_to_file(withTypeInfoCode, self.BLOB_NAME_WITHOUT_OPTIONALS)

        readWithTypeInfoCode = zserio.deserialize_from_file(self.api.WithTypeInfoCode,
                                                            self.BLOB_NAME_WITHOUT_OPTIONALS)
        self.assertEqual(withTypeInfoCode, readWithTypeInfoCode)

    def _checkSimpleStruct(self, type_info):
        self.assertEqual("with_type_info_code.SimpleStruct", type_info.schema_name)
        self.assertEqual(self.api.SimpleStruct, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(7, len(fields))

        # fieldU32
        memberInfo = fields[0]
        self.assertEqual("fieldU32", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(3, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_u32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ALIGN, memberInfo.attributes)
        self.assertEqual(8, memberInfo.attributes[MemberAttribute.ALIGN]())
        self.assertIn(MemberAttribute.INITIALIZER, memberInfo.attributes)
        self.assertEqual(10, memberInfo.attributes[MemberAttribute.INITIALIZER]())

        # fieldOffset
        memberInfo = fields[1]
        self.assertEqual("fieldOffset", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_offset", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldString
        memberInfo = fields[2]
        self.assertEqual("fieldString", memberInfo.schema_name)
        self.assertEqual("string", memberInfo.type_info.schema_name)
        self.assertEqual(str, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(3, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_string", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OFFSET, memberInfo.attributes)
        offset_lambda = memberInfo.attributes[MemberAttribute.OFFSET]
        self.assertEqual(13, offset_lambda(self.api.SimpleStruct(field_offset_=13), None))
        self.assertIn(MemberAttribute.INITIALIZER, memberInfo.attributes)
        initializer_lambda = memberInfo.attributes[MemberAttribute.INITIALIZER]
        self.assertEqual("MyString", initializer_lambda())

        # fieldBool
        memberInfo = fields[3]
        self.assertEqual("fieldBool", memberInfo.schema_name)
        self.assertEqual("bool", memberInfo.type_info.schema_name)
        self.assertEqual(bool, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_bool", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.INITIALIZER, memberInfo.attributes)
        self.assertEqual(False, memberInfo.attributes[MemberAttribute.INITIALIZER]())

        # fieldFloat16
        memberInfo = fields[4]
        self.assertEqual("fieldFloat16", memberInfo.schema_name)
        self.assertEqual("float16", memberInfo.type_info.schema_name)
        self.assertEqual(float, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_float16", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.INITIALIZER, memberInfo.attributes)
        self.assertEqual(1.0, memberInfo.attributes[MemberAttribute.INITIALIZER]())

        # fieldFloat32
        memberInfo = fields[5]
        self.assertEqual("fieldFloat32", memberInfo.schema_name)
        self.assertEqual("float32", memberInfo.type_info.schema_name)
        self.assertEqual(float, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_float32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldFloat64
        memberInfo = fields[6]
        self.assertEqual("fieldFloat64", memberInfo.schema_name)
        self.assertEqual("float64", memberInfo.type_info.schema_name)
        self.assertEqual(float, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_float64", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.INITIALIZER, memberInfo.attributes)
        self.assertEqual(2.0, memberInfo.attributes[MemberAttribute.INITIALIZER]())

    def _checkComplexStruct(self, type_info):
        self.assertEqual("with_type_info_code.ComplexStruct", type_info.schema_name)
        self.assertEqual(self.api.ComplexStruct, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(14, len(fields))
        self.assertIn(TypeAttribute.FUNCTIONS, type_info.attributes)
        functions = type_info.attributes[TypeAttribute.FUNCTIONS]
        self.assertEqual(1, len(functions))

        # simpleStruct
        memberInfo = fields[0]
        self.assertEqual("simpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("simple_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # anotherSimpleStruct
        memberInfo = fields[1]
        self.assertEqual("anotherSimpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("another_simple_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # optionalSimpleStruct
        memberInfo = fields[2]
        self.assertEqual("optionalSimpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("optional_simple_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_simple_struct_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_simple_struct_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # array
        memberInfo = fields[3]
        self.assertEqual("array", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(3, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])
        self.assertIn(MemberAttribute.CONSTRAINT, memberInfo.attributes)
        constraint_lambda = memberInfo.attributes[MemberAttribute.CONSTRAINT]
        self.assertEqual(False, constraint_lambda(self.api.ComplexStruct(array_=[])))

        # arrayWithLen
        memberInfo = fields[4]
        self.assertEqual("arrayWithLen", memberInfo.schema_name)
        self.assertEqual("int:5", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(5, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("array_with_len", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        array_length_lambda = memberInfo.attributes[MemberAttribute.ARRAY_LENGTH]
        self.assertEqual(0, array_length_lambda(self.api.ComplexStruct(array_=[0])))
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        optional_lambda = memberInfo.attributes[MemberAttribute.OPTIONAL]
        self.assertEqual(False, optional_lambda(self.api.ComplexStruct(array_=[0])))
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_array_with_len_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_array_with_len_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # paramStructArray
        memberInfo = fields[5]
        self.assertEqual("paramStructArray", memberInfo.schema_name)
        self._checkParameterizedStruct(memberInfo.type_info)
        self.assertEqual(6, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("param_struct_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertEqual(None, memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_param_struct_array_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_param_struct_array_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        self.assertEqual(self.api.ComplexStruct().simple_struct,
                         type_argument_lambda(self.api.ComplexStruct(), 0))
        self.assertEqual(self.api.ComplexStruct().another_simple_struct,
                         type_argument_lambda(self.api.ComplexStruct(), 1))
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertEqual(None, memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

        # dynamicBitField
        memberInfo = fields[6]
        self.assertEqual("dynamicBitField", memberInfo.schema_name)
        self.assertEqual("bit", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("dynamic_bit_field", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        self.assertEqual(13, type_argument_lambda(
            self.api.ComplexStruct(simple_struct_=self.api.SimpleStruct(field_u32_=13)), None
        ))

        # dynamicBitFieldArray
        memberInfo = fields[7]
        self.assertEqual("dynamicBitFieldArray", memberInfo.schema_name)
        self.assertEqual("bit", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("dynamic_bit_field_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.PACKED, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.PACKED])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        # self.dynamic_bit_field * 2
        self.assertEqual(5 * 2, type_argument_lambda(self.api.ComplexStruct(dynamic_bit_field_=5), None))
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertEqual(None, memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

        # optionalEnum
        memberInfo = fields[8]
        self.assertEqual("optionalEnum", memberInfo.schema_name)
        self._checkTestEnum(memberInfo.type_info)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("optional_enum", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_enum_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_enum_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # optionalBitmask
        memberInfo = fields[9]
        self.assertEqual("optionalBitmask", memberInfo.schema_name)
        self._checkTestBitmask(memberInfo.type_info)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("optional_bitmask", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_bitmask_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_bitmask_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # optionalExtern
        memberInfo = fields[10]
        self.assertEqual("optionalExtern", memberInfo.schema_name)
        self.assertEqual("extern", memberInfo.type_info.schema_name)
        self.assertEqual(zserio.BitBuffer, memberInfo.type_info.py_type)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("optional_extern", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_extern_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_extern_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # optionalBytes
        memberInfo = fields[11]
        self.assertEqual("optionalBytes", memberInfo.schema_name)
        self.assertEqual("bytes", memberInfo.type_info.schema_name)
        self.assertEqual(bytearray, memberInfo.type_info.py_type)
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("optional_bytes", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_bytes_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_optional_bytes_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # enumArray
        memberInfo = fields[12]
        self.assertEqual("enumArray", memberInfo.schema_name)
        self._checkTestEnum(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("enum_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        arrayLengthLambda = memberInfo.attributes[MemberAttribute.ARRAY_LENGTH]
        self.assertEqual(2, arrayLengthLambda(self.api.ComplexStruct(array_=[])))

        # bitmaskArray
        memberInfo = fields[13]
        self.assertEqual("bitmaskArray", memberInfo.schema_name)
        self._checkTestBitmask(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("bitmask_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        arrayLengthLambda = memberInfo.attributes[MemberAttribute.ARRAY_LENGTH]
        self.assertEqual(5, arrayLengthLambda(self.api.ComplexStruct(array_=[])))

        # firstArrayElement
        memberInfo = functions[0]
        self.assertEqual("firstArrayElement", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.FUNCTION_NAME, memberInfo.attributes)
        self.assertEqual("first_array_element", memberInfo.attributes[MemberAttribute.FUNCTION_NAME])
        self.assertIn(MemberAttribute.FUNCTION_RESULT, memberInfo.attributes)
        function_result_lambda = memberInfo.attributes[MemberAttribute.FUNCTION_RESULT]
        self.assertEqual(1, function_result_lambda(self.api.ComplexStruct(array_=[1])))

    def _checkParameterizedStruct(self, type_info):
        self.assertEqual("with_type_info_code.ParameterizedStruct", type_info.schema_name)
        self.assertEqual(self.api.ParameterizedStruct, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.PARAMETERS, type_info.attributes)
        parameters = type_info.attributes[TypeAttribute.PARAMETERS]
        self.assertEqual(1, len(parameters))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(1, len(fields))

        # simple
        memberInfo = parameters[0]
        self.assertEqual("simple", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("simple", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # array
        memberInfo = fields[0]
        self.assertEqual("array", memberInfo.schema_name)
        self.assertEqual("uint8", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        array_length_lambda = memberInfo.attributes[MemberAttribute.ARRAY_LENGTH]
        self.assertEqual(13, array_length_lambda(
            self.api.ParameterizedStruct(self.api.SimpleStruct(field_u32_=13))
        ))

    def _checkRecursiveStruct(self, type_info):
        self.assertEqual("with_type_info_code.RecursiveStruct", type_info.schema_name)
        self.assertEqual(self.api.RecursiveStruct, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(3, len(fields))

        # fieldU32
        memberInfo = fields[0]
        self.assertEqual("fieldU32", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_u32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldRecursion
        memberInfo = fields[1]
        self.assertEqual("fieldRecursion", memberInfo.schema_name)
        self.assertEqual(type_info.schema_name, memberInfo.type_info.schema_name)
        self.assertEqual(type_info.py_type, memberInfo.type_info.py_type)
        self.assertEqual(len(type_info.attributes[TypeAttribute.FIELDS]),
                         len(memberInfo.type_info.attributes[TypeAttribute.FIELDS]))
        self.assertEqual(4, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_recursion", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.OPTIONAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.OPTIONAL])
        self.assertIn(MemberAttribute.IS_USED_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_field_recursion_used",
                         memberInfo.attributes[MemberAttribute.IS_USED_INDICATOR_NAME])
        self.assertIn(MemberAttribute.IS_SET_INDICATOR_NAME, memberInfo.attributes)
        self.assertEqual("is_field_recursion_set",
                         memberInfo.attributes[MemberAttribute.IS_SET_INDICATOR_NAME])

        # arrayRecursion
        memberInfo = fields[2]
        self.assertEqual("arrayRecursion", memberInfo.schema_name)
        self.assertEqual(type_info.schema_name, memberInfo.type_info.schema_name)
        self.assertEqual(type_info.py_type, memberInfo.type_info.py_type)
        self.assertEqual(len(type_info.attributes[TypeAttribute.FIELDS]),
                         len(memberInfo.type_info.attributes[TypeAttribute.FIELDS]))
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("array_recursion", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

    def _checkRecursiveUnion(self, type_info):
        self.assertEqual("with_type_info_code.RecursiveUnion", type_info.schema_name)
        self.assertEqual(self.api.RecursiveUnion, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertIn(TypeAttribute.SELECTOR, type_info.attributes)
        self.assertEqual(None, type_info.attributes[TypeAttribute.SELECTOR])
        self.assertEqual(2, len(fields))

        # fieldU32
        memberInfo = fields[0]
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_u32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # recursive
        memberInfo = fields[1]
        self.assertEqual("recursive", memberInfo.schema_name)
        self.assertEqual(type_info.schema_name, memberInfo.type_info.schema_name)
        self.assertEqual(type_info.py_type, memberInfo.type_info.py_type)
        self.assertEqual(len(type_info.attributes[TypeAttribute.FIELDS]),
                         len(memberInfo.type_info.attributes[TypeAttribute.FIELDS]))
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("recursive", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertEqual(None, memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

    def _checkRecursiveChoice(self, type_info):
        self.assertEqual("with_type_info_code.RecursiveChoice", type_info.schema_name)
        self.assertEqual(self.api.RecursiveChoice, type_info.py_type)
        self.assertEqual(4, len(type_info.attributes))
        self.assertIn(TypeAttribute.PARAMETERS, type_info.attributes)
        parameters = type_info.attributes[TypeAttribute.PARAMETERS]
        self.assertEqual(2, len(parameters))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(2, len(fields))
        self.assertIn(TypeAttribute.SELECTOR, type_info.attributes)
        selector_lambda = type_info.attributes[TypeAttribute.SELECTOR]
        self.assertEqual(True, selector_lambda(self.api.RecursiveChoice(True, False)))
        self.assertIn(TypeAttribute.CASES, type_info.attributes)
        cases = type_info.attributes[TypeAttribute.CASES]
        self.assertEqual(2, len(cases))

        # param1
        memberInfo = parameters[0]
        self.assertEqual("param1", memberInfo.schema_name)
        self.assertEqual("bool", memberInfo.type_info.schema_name)
        self.assertEqual(bool, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("param1", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # param2
        memberInfo = parameters[1]
        self.assertEqual("param2", memberInfo.schema_name)
        self.assertEqual("bool", memberInfo.type_info.schema_name)
        self.assertEqual(bool, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("param2", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # case true
        case_info = cases[0]
        self.assertEqual(1, len(case_info.case_expressions))
        self.assertEqual(True, case_info.case_expressions[0]())
        self.assertEqual(fields[0].schema_name, case_info.field.schema_name)

        # case false
        case_info = cases[1]
        self.assertEqual(1, len(case_info.case_expressions))
        self.assertEqual(False, case_info.case_expressions[0]())
        self.assertEqual(fields[1].schema_name, case_info.field.schema_name)

        # recursive
        memberInfo = fields[0]
        self.assertEqual("recursive", memberInfo.schema_name)
        self.assertEqual(type_info.schema_name, memberInfo.type_info.schema_name)
        self.assertEqual(type_info.py_type, memberInfo.type_info.py_type)
        self.assertEqual(len(type_info.attributes[TypeAttribute.FIELDS]),
                         len(memberInfo.type_info.attributes[TypeAttribute.FIELDS]))
        self.assertEqual(3, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("recursive", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertEqual(None, memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(2, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_1_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        type_argument_2_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][1]
        self.assertEqual(False, type_argument_1_lambda(self.api.RecursiveChoice(True, False), None))
        self.assertEqual(False, type_argument_2_lambda(self.api.RecursiveChoice(True, False), None))

        # fieldU32
        memberInfo = fields[1]
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_u32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

    def _checkTestEnum(self, type_info):
        self.assertEqual("with_type_info_code.TestEnum", type_info.schema_name)
        self.assertEqual(self.api.TestEnum, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.UNDERLYING_TYPE, type_info.attributes)
        underlying_info = type_info.attributes[TypeAttribute.UNDERLYING_TYPE]
        self.assertEqual("with_type_info_code.EnumUnderlyingType", underlying_info.schema_name)
        self.assertEqual(int, underlying_info.py_type)
        self.assertFalse(underlying_info.attributes)
        self.assertIn(TypeAttribute.ENUM_ITEMS, type_info.attributes)
        items = type_info.attributes[TypeAttribute.ENUM_ITEMS]
        self.assertEqual(3, len(items))

        # One
        item_info = items[0]
        self.assertEqual("One", item_info.schema_name)
        self.assertEqual(self.api.TestEnum.ONE, item_info.py_item)

        # TWO
        item_info = items[1]
        self.assertEqual("_TWO", item_info.schema_name)
        self.assertEqual(self.api.TestEnum._TWO, item_info.py_item)

        # ThreeItem
        item_info = items[2]
        self.assertEqual("ItemThree", item_info.schema_name)
        self.assertEqual(self.api.TestEnum.ITEM_THREE, item_info.py_item)

    def _checkTestBitmask(self, type_info):
        self.assertEqual("with_type_info_code.TestBitmask", type_info.schema_name)
        self.assertEqual(self.api.TestBitmask, type_info.py_type)
        self.assertEqual(3, len(type_info.attributes))
        self.assertIn(TypeAttribute.UNDERLYING_TYPE, type_info.attributes)
        underlying_info = type_info.attributes[TypeAttribute.UNDERLYING_TYPE]
        self.assertEqual("bit", underlying_info.schema_name)
        self.assertEqual(int, underlying_info.py_type)
        self.assertFalse(underlying_info.attributes)
        self.assertIn(TypeAttribute.UNDERLYING_TYPE_ARGUMENTS, type_info.attributes)
        underlying_args = type_info.attributes[TypeAttribute.UNDERLYING_TYPE_ARGUMENTS]
        self.assertEqual(1, len(underlying_args))
        self.assertEqual(10, underlying_args[0]())
        self.assertIn(TypeAttribute.BITMASK_VALUES, type_info.attributes)
        items = type_info.attributes[TypeAttribute.BITMASK_VALUES]
        self.assertEqual(3, len(items))

        # RED
        item_info = items[0]
        self.assertEqual("RED", item_info.schema_name)
        self.assertEqual(self.api.TestBitmask.Values.RED, item_info.py_item)

        # Green
        item_info = items[1]
        self.assertEqual("_Green", item_info.schema_name)
        self.assertEqual(self.api.TestBitmask.Values._GREEN, item_info.py_item)

        # Color Blue
        item_info = items[2]
        self.assertEqual("ColorBlue", item_info.schema_name)
        self.assertEqual(self.api.TestBitmask.Values.COLOR_BLUE, item_info.py_item)

    def _checkSimpleUnion(self, type_info):
        self.assertEqual("with_type_info_code.SimpleUnion", type_info.schema_name)
        self.assertEqual(self.api.SimpleUnion, type_info.py_type)
        self.assertEqual(3, len(type_info.attributes))
        self.assertIn(TypeAttribute.SELECTOR, type_info.attributes)
        self.assertIsNone(type_info.attributes[TypeAttribute.SELECTOR])
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(2, len(fields))
        self.assertIn(TypeAttribute.FUNCTIONS, type_info.attributes)
        functions = type_info.attributes[TypeAttribute.FUNCTIONS]
        self.assertEqual(1, len(functions))

        # testBitmask
        memberInfo = fields[0]
        self.assertEqual("testBitmask", memberInfo.schema_name)
        self._checkTestBitmask(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("test_bitmask", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # simpleStruct
        memberInfo = fields[1]
        self.assertEqual("simpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("simple_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # simpleStructFieldU32
        memberInfo = functions[0]
        self.assertEqual("simpleStructFieldU32", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.FUNCTION_NAME, memberInfo.attributes)
        self.assertEqual("simple_struct_field_u32", memberInfo.attributes[MemberAttribute.FUNCTION_NAME])
        self.assertIn(MemberAttribute.FUNCTION_RESULT, memberInfo.attributes)
        function_result_lambda = memberInfo.attributes[MemberAttribute.FUNCTION_RESULT]
        self.assertEqual(13, function_result_lambda(
            self.api.SimpleUnion(simple_struct_=self.api.SimpleStruct(field_u32_=13))
        ))

    def _checkSimpleChoice(self, type_info):
        self.assertEqual("with_type_info_code.SimpleChoice", type_info.schema_name)
        self.assertEqual(self.api.SimpleChoice, type_info.py_type)
        self.assertEqual(5, len(type_info.attributes))
        self.assertIn(TypeAttribute.PARAMETERS, type_info.attributes)
        parameters = type_info.attributes[TypeAttribute.PARAMETERS]
        self.assertEqual(1, len(parameters))
        self.assertIn(TypeAttribute.SELECTOR, type_info.attributes)
        selector_lambda = type_info.attributes[TypeAttribute.SELECTOR]
        self.assertEqual(self.api.TestEnum._TWO, selector_lambda(self.api.SimpleChoice(self.api.TestEnum._TWO)))
        self.assertIn(TypeAttribute.CASES, type_info.attributes)
        cases = type_info.attributes[TypeAttribute.CASES]
        self.assertEqual(3, len(cases))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(2, len(fields))
        self.assertIn(TypeAttribute.FUNCTIONS, type_info.attributes)
        functions = type_info.attributes[TypeAttribute.FUNCTIONS]
        self.assertEqual(1, len(functions))

        # selector
        memberInfo = parameters[0]
        self.assertEqual("selector", memberInfo.schema_name)
        self._checkTestEnum(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("selector", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # case One
        case_info = cases[0]
        self.assertEqual(1, len(case_info.case_expressions))
        self.assertEqual(self.api.TestEnum.ONE, case_info.case_expressions[0]())
        self.assertIsNone(case_info.field)

        # case TWO
        case_info = cases[1]
        self.assertEqual(1, len(case_info.case_expressions))
        self.assertEqual(self.api.TestEnum._TWO, case_info.case_expressions[0]())
        self.assertIsNotNone(case_info.field)
        self.assertEqual("fieldTwo", case_info.field.schema_name)
        self._checkSimpleUnion(case_info.field.type_info)
        self.assertEqual(1, len(case_info.field.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, case_info.field.attributes)
        self.assertEqual("field_two", case_info.field.attributes[MemberAttribute.PROPERTY_NAME])

        # default
        case_info = cases[2]
        self.assertEqual(0, len(case_info.case_expressions))
        self.assertIsNotNone(case_info.field)
        self.assertEqual("fieldDefault", case_info.field.schema_name)
        self.assertEqual("string", case_info.field.type_info.schema_name)
        self.assertEqual(str, case_info.field.type_info.py_type)
        self.assertFalse(case_info.field.type_info.attributes)
        self.assertEqual(1, len(case_info.field.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, case_info.field.attributes)
        self.assertEqual("field_default", case_info.field.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldTwo
        memberInfo = fields[0]
        self.assertEqual("fieldTwo", memberInfo.schema_name)
        self._checkSimpleUnion(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_two", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldDefault
        memberInfo = fields[1]
        self.assertEqual("fieldDefault", memberInfo.schema_name)
        self.assertEqual("string", memberInfo.type_info.schema_name)
        self.assertEqual(str, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field_default", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fieldTwoFuncCall
        memberInfo = functions[0]
        self.assertEqual("fieldTwoFuncCall", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.FUNCTION_NAME, memberInfo.attributes)
        self.assertEqual("field_two_func_call", memberInfo.attributes[MemberAttribute.FUNCTION_NAME])
        self.assertIn(MemberAttribute.FUNCTION_RESULT, memberInfo.attributes)
        function_result_lambda = memberInfo.attributes[MemberAttribute.FUNCTION_RESULT]
        self.assertEqual(42, function_result_lambda(
            self.api.SimpleChoice(self.api.TestEnum._TWO, field_two_=self.api.SimpleUnion(
                simple_struct_=self.api.SimpleStruct(field_u32_=42)
            ))
        ))

    def _checkTS32(self, type_info):
        self.assertEqual("with_type_info_code.TS32", type_info.schema_name)
        self.assertEqual(self.api.TS32, type_info.py_type)
        self.assertEqual(3, len(type_info.attributes))
        self.assertIn(TypeAttribute.TEMPLATE_NAME, type_info.attributes)
        self.assertEqual("with_type_info_code.TemplatedStruct",
                         type_info.attributes[TypeAttribute.TEMPLATE_NAME])
        self.assertIn(TypeAttribute.TEMPLATE_ARGUMENTS, type_info.attributes)
        template_args = type_info.attributes[TypeAttribute.TEMPLATE_ARGUMENTS]
        self.assertEqual(1, len(template_args))
        arg = template_args[0]
        self.assertIsInstance(arg, TypeInfo)
        self.assertEqual("uint32", arg.schema_name)
        self.assertEqual(int, arg.py_type)
        self.assertFalse(arg.attributes)
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(1, len(fields))

        # field
        memberInfo = fields[0]
        self.assertEqual("field", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("field", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

    def _checkTemplatedParameterizedStruct_TS32(self, type_info):
        self.assertEqual("with_type_info_code.TemplatedParameterizedStruct_TS32",
                         type_info.schema_name)
        self.assertEqual(self.api.TemplatedParameterizedStruct_TS32, type_info.py_type)
        self.assertEqual(4, len(type_info.attributes))
        self.assertIn(TypeAttribute.TEMPLATE_NAME, type_info.attributes)
        self.assertEqual("with_type_info_code.TemplatedParameterizedStruct",
                         type_info.attributes[TypeAttribute.TEMPLATE_NAME])
        self.assertIn(TypeAttribute.TEMPLATE_ARGUMENTS, type_info.attributes)
        template_args = type_info.attributes[TypeAttribute.TEMPLATE_ARGUMENTS]
        self.assertEqual(1, len(template_args))
        arg = template_args[0]
        self.assertIsInstance(arg, TypeInfo)
        self._checkTS32(arg)
        self.assertIn(TypeAttribute.PARAMETERS, type_info.attributes)
        parameters = type_info.attributes[TypeAttribute.PARAMETERS]
        self.assertEqual(1, len(parameters))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(1, len(fields))

        # param
        memberInfo = parameters[0]
        self.assertEqual("param", memberInfo.schema_name)
        self._checkTS32(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("param", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # array
        memberInfo = fields[0]
        self.assertEqual("array", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes) # self.param.field
        array_length_lambda = memberInfo.attributes[MemberAttribute.ARRAY_LENGTH]
        self.assertEqual(2, array_length_lambda(
            self.api.TemplatedParameterizedStruct_TS32(self.api.TS32(field_=2))
        ))

    def _checkWithTypeInfoCode(self, type_info):
        self.assertEqual("with_type_info_code.WithTypeInfoCode", type_info.schema_name)
        self.assertEqual(self.api.WithTypeInfoCode, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.FIELDS, type_info.attributes)
        fields = type_info.attributes[TypeAttribute.FIELDS]
        self.assertEqual(15, len(fields))

        # simpleStruct
        memberInfo = fields[0]
        self.assertEqual("simpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("simple_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # complexStruct
        memberInfo = fields[1]
        self.assertEqual("complexStruct", memberInfo.schema_name)
        self._checkComplexStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("complex_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # parameterizedStruct
        memberInfo = fields[2]
        self.assertEqual("parameterizedStruct", memberInfo.schema_name)
        self._checkParameterizedStruct(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("parameterized_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes) # self.simple_struct
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        self.assertEqual(None, type_argument_lambda(self.api.WithTypeInfoCode(), None))

        # recursiveStruct
        memberInfo = fields[3]
        self.assertEqual("recursiveStruct", memberInfo.schema_name)
        self._checkRecursiveStruct(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("recursive_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # recursiveUnion
        memberInfo = fields[4]
        self.assertEqual("recursiveUnion", memberInfo.schema_name)
        self._checkRecursiveUnion(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("recursive_union", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # recursiveChoice
        memberInfo = fields[5]
        self.assertEqual("recursiveChoice", memberInfo.schema_name)
        self._checkRecursiveChoice(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("recursive_choice", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(2, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        self.assertEqual(True, memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0](object(), None))
        self.assertEqual(False, memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][1](object(), None))

        # selector
        memberInfo = fields[6]
        self.assertEqual("selector", memberInfo.schema_name)
        self._checkTestEnum(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("selector", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # simpleChoice
        memberInfo = fields[7]
        self.assertEqual("simpleChoice", memberInfo.schema_name)
        self._checkSimpleChoice(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("simple_choice", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes) # self.selector
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        self.assertEqual(self.api.TestEnum.ONE, type_argument_lambda(
            self.api.WithTypeInfoCode(selector_=self.api.TestEnum.ONE), None
        ))

        # templatedStruct
        memberInfo = fields[8]
        self.assertEqual("templatedStruct", memberInfo.schema_name)
        self._checkTS32(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("templated_struct", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # templatedParameterizedStruct
        memberInfo = fields[9]
        self.assertEqual("templatedParameterizedStruct", memberInfo.schema_name)
        self._checkTemplatedParameterizedStruct_TS32(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("templated_parameterized_struct",
                         memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.TYPE_ARGUMENTS, memberInfo.attributes)
        self.assertEqual(1, len(memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS]))
        type_argument_lambda = memberInfo.attributes[MemberAttribute.TYPE_ARGUMENTS][0]
        self.assertEqual(None, type_argument_lambda(self.api.WithTypeInfoCode(), None))

        # externData
        memberInfo = fields[10]
        self.assertEqual("externData", memberInfo.schema_name)
        self.assertEqual("extern", memberInfo.type_info.schema_name)
        self.assertEqual(zserio.BitBuffer, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("extern_data", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # externArray
        memberInfo = fields[11]
        self.assertEqual("externArray", memberInfo.schema_name)
        self.assertEqual("extern", memberInfo.type_info.schema_name)
        self.assertEqual(zserio.BitBuffer, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("extern_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

        # bytesData
        memberInfo = fields[12]
        self.assertEqual("bytesData", memberInfo.schema_name)
        self.assertEqual("bytes", memberInfo.type_info.schema_name)
        self.assertEqual(bytearray, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("bytes_data", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # bytesArray
        memberInfo = fields[13]
        self.assertEqual("bytesArray", memberInfo.schema_name)
        self.assertEqual("bytes", memberInfo.type_info.schema_name)
        self.assertEqual(bytearray, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("bytes_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

        # implicitArray
        memberInfo = fields[14]
        self.assertEqual("implicitArray", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(3, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("implicit_array", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])
        self.assertIn(MemberAttribute.IMPLICIT, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.IMPLICIT])
        self.assertIn(MemberAttribute.ARRAY_LENGTH, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.ARRAY_LENGTH])

    def _checkSqlTable(self, type_info):
        self.assertEqual("with_type_info_code.SqlTable", type_info.schema_name)
        self.assertEqual(self.api.SqlTable, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.COLUMNS, type_info.attributes)
        columns = type_info.attributes[TypeAttribute.COLUMNS]
        self.assertEqual(2, len(columns))

        # pk
        memberInfo = columns[0]
        self.assertEqual("pk", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.SQL_CONSTRAINT, memberInfo.attributes)
        self.assertEqual("PRIMARY KEY NOT NULL", memberInfo.attributes[MemberAttribute.SQL_CONSTRAINT])

        # text
        memberInfo = columns[1]
        self.assertEqual("text", memberInfo.schema_name)
        self.assertEqual("string", memberInfo.type_info.schema_name)
        self.assertEqual(str, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("TEXT", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])

    def _checkTemplatedSqlTable_uint32(self, type_info):
        self.assertEqual("with_type_info_code.TemplatedSqlTable_uint32", type_info.schema_name)
        self.assertEqual(self.api.TemplatedSqlTable_uint32, type_info.py_type)
        self.assertEqual(4, len(type_info.attributes))
        self.assertIn(TypeAttribute.TEMPLATE_NAME, type_info.attributes)
        self.assertEqual("with_type_info_code.TemplatedSqlTable",
                         type_info.attributes[TypeAttribute.TEMPLATE_NAME])
        self.assertIn(TypeAttribute.TEMPLATE_ARGUMENTS, type_info.attributes)
        template_args = type_info.attributes[TypeAttribute.TEMPLATE_ARGUMENTS]
        self.assertEqual(1, len(template_args))
        arg = template_args[0]
        self.assertIsInstance(arg, TypeInfo)
        self.assertEqual("uint32", arg.schema_name)
        self.assertEqual(int, arg.py_type)
        self.assertFalse(arg.attributes)
        self.assertIn(TypeAttribute.COLUMNS, type_info.attributes)
        columns = type_info.attributes[TypeAttribute.COLUMNS]
        self.assertEqual(2, len(columns))
        self.assertIn(TypeAttribute.SQL_CONSTRAINT, type_info.attributes)
        self.assertEqual("PRIMARY KEY(pk)", type_info.attributes[TypeAttribute.SQL_CONSTRAINT])

        # pk
        memberInfo = columns[0]
        self.assertEqual("pk", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.SQL_CONSTRAINT, memberInfo.attributes)
        self.assertEqual("NOT NULL", memberInfo.attributes[MemberAttribute.SQL_CONSTRAINT])

        # withTypeInfoCode
        memberInfo = columns[1]
        self.assertEqual("withTypeInfoCode", memberInfo.schema_name)
        self._checkWithTypeInfoCode(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("BLOB", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])

    def _checkTemplatedSqlTableU8(self, type_info):
        self.assertEqual("with_type_info_code.TemplatedSqlTableU8", type_info.schema_name)
        self.assertEqual(self.api.TemplatedSqlTableU8, type_info.py_type)
        self.assertEqual(4, len(type_info.attributes))
        self.assertIn(TypeAttribute.TEMPLATE_NAME, type_info.attributes)
        self.assertEqual("with_type_info_code.TemplatedSqlTable",
                         type_info.attributes[TypeAttribute.TEMPLATE_NAME])
        self.assertIn(TypeAttribute.TEMPLATE_ARGUMENTS, type_info.attributes)
        template_args = type_info.attributes[TypeAttribute.TEMPLATE_ARGUMENTS]
        self.assertEqual(1, len(template_args))
        arg = template_args[0]
        self.assertIsInstance(arg, TypeInfo)
        self.assertEqual("uint8", arg.schema_name)
        self.assertEqual(int, arg.py_type)
        self.assertFalse(arg.attributes)
        self.assertIn(TypeAttribute.COLUMNS, type_info.attributes)
        columns = type_info.attributes[TypeAttribute.COLUMNS]
        self.assertEqual(2, len(columns))
        self.assertIn(TypeAttribute.SQL_CONSTRAINT, type_info.attributes)
        self.assertEqual("PRIMARY KEY(pk)", type_info.attributes[TypeAttribute.SQL_CONSTRAINT])

        # pk
        memberInfo = columns[0]
        self.assertEqual("pk", memberInfo.schema_name)
        self.assertEqual("uint8", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.SQL_CONSTRAINT, memberInfo.attributes)
        self.assertEqual("NOT NULL", memberInfo.attributes[MemberAttribute.SQL_CONSTRAINT])

        # withTypeInfoCode
        memberInfo = columns[1]
        self.assertEqual("withTypeInfoCode", memberInfo.schema_name)
        self._checkWithTypeInfoCode(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("BLOB", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])

    def _checkFts4Table(self, type_info):
        self.assertEqual("with_type_info_code.Fts4Table", type_info.schema_name)
        self.assertEqual(self.api.Fts4Table, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.VIRTUAL_TABLE_USING, type_info.attributes)
        self.assertEqual("fts4", type_info.attributes[TypeAttribute.VIRTUAL_TABLE_USING])
        self.assertIn(TypeAttribute.COLUMNS, type_info.attributes)
        columns = type_info.attributes[TypeAttribute.COLUMNS]
        self.assertEqual(2, len(columns))

        # docId
        memberInfo = columns[0]
        self.assertEqual("docId", memberInfo.schema_name)
        self.assertEqual("int64", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.VIRTUAL, memberInfo.attributes)
        self.assertIsNone(memberInfo.attributes[MemberAttribute.VIRTUAL])

        # searchTags
        memberInfo = columns[1]
        self.assertEqual("searchTags", memberInfo.schema_name)
        self.assertEqual("string", memberInfo.type_info.schema_name)
        self.assertEqual(str, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("TEXT", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])

    def _checkWithoutRowIdTable(self, type_info):
        self.assertEqual("with_type_info_code.WithoutRowIdTable", type_info.schema_name)
        self.assertEqual(self.api.WithoutRowIdTable, type_info.py_type)
        self.assertEqual(3, len(type_info.attributes))
        self.assertIn(TypeAttribute.WITHOUT_ROWID, type_info.attributes)
        self.assertIsNone(type_info.attributes[TypeAttribute.WITHOUT_ROWID])
        self.assertIn(TypeAttribute.COLUMNS, type_info.attributes)
        columns = type_info.attributes[TypeAttribute.COLUMNS]
        self.assertEqual(2, len(columns))
        self.assertIn(TypeAttribute.SQL_CONSTRAINT, type_info.attributes)
        self.assertEqual("PRIMARY KEY(pk1, pk2)", type_info.attributes[TypeAttribute.SQL_CONSTRAINT])

        # pk1
        memberInfo = columns[0]
        self.assertEqual("pk1", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.SQL_CONSTRAINT, memberInfo.attributes)
        self.assertEqual("NOT NULL", memberInfo.attributes[MemberAttribute.SQL_CONSTRAINT])

        # pk2
        memberInfo = columns[1]
        self.assertEqual("pk2", memberInfo.schema_name)
        self.assertEqual("uint32", memberInfo.type_info.schema_name)
        self.assertEqual(int, memberInfo.type_info.py_type)
        self.assertFalse(memberInfo.type_info.attributes)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.SQL_TYPE_NAME, memberInfo.attributes)
        self.assertEqual("INTEGER", memberInfo.attributes[MemberAttribute.SQL_TYPE_NAME])
        self.assertIn(MemberAttribute.SQL_CONSTRAINT, memberInfo.attributes)
        self.assertEqual("NOT NULL", memberInfo.attributes[MemberAttribute.SQL_CONSTRAINT])

    def _checkSqlDatabase(self, type_info):
        self.assertEqual("with_type_info_code.SqlDatabase", type_info.schema_name)
        self.assertEqual(self.api.SqlDatabase, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.TABLES, type_info.attributes)
        tables = type_info.attributes[TypeAttribute.TABLES]
        self.assertEqual(5, len(tables))

        # sqlTable
        memberInfo = tables[0]
        self.assertEqual("sqlTable", memberInfo.schema_name)
        self._checkSqlTable(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("sql_table", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # templatedSqlTableU32
        memberInfo = tables[1]
        self.assertEqual("templatedSqlTableU32", memberInfo.schema_name)
        self._checkTemplatedSqlTable_uint32(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("templated_sql_table_u32", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # templatedSqlTableU8
        memberInfo = tables[2]
        self.assertEqual("templatedSqlTableU8", memberInfo.schema_name)
        self._checkTemplatedSqlTableU8(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("templated_sql_table_u8", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # fts4Table
        memberInfo = tables[3]
        self.assertEqual("fts4Table", memberInfo.schema_name)
        self._checkFts4Table(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("fts4_table", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

        # withoutRowIdTable
        memberInfo = tables[4]
        self.assertEqual("withoutRowIdTable", memberInfo.schema_name)
        self._checkWithoutRowIdTable(memberInfo.type_info)
        self.assertEqual(1, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.PROPERTY_NAME, memberInfo.attributes)
        self.assertEqual("without_row_id_table", memberInfo.attributes[MemberAttribute.PROPERTY_NAME])

    def _checkSimplePubsub(self, type_info):
        self.assertEqual("with_type_info_code.SimplePubsub", type_info.schema_name)
        self.assertEqual(self.api.SimplePubsub, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.MESSAGES, type_info.attributes)
        messages = type_info.attributes[TypeAttribute.MESSAGES]
        self.assertEqual(2, len(messages))

        # pubSimpleStruct
        memberInfo = messages[0]
        self.assertEqual("pubSimpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes), memberInfo.attributes)
        self.assertIn(MemberAttribute.TOPIC, memberInfo.attributes)
        self.assertEqual("simpleStruct", memberInfo.attributes[MemberAttribute.TOPIC])
        self.assertIn(MemberAttribute.PUBLISH, memberInfo.attributes)
        self.assertEqual("publish_pub_simple_struct", memberInfo.attributes[MemberAttribute.PUBLISH])

        # subSimpleStruct
        memberInfo = messages[1]
        self.assertEqual("subSimpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes), memberInfo.attributes)
        self.assertIn(MemberAttribute.TOPIC, memberInfo.attributes)
        self.assertEqual("simpleStruct", memberInfo.attributes[MemberAttribute.TOPIC])
        self.assertIn(MemberAttribute.SUBSCRIBE, memberInfo.attributes)
        self.assertEqual("subscribe_sub_simple_struct", memberInfo.attributes[MemberAttribute.SUBSCRIBE])

    def _checkSimpleService(self, type_info):
        self.assertEqual("with_type_info_code.SimpleService", type_info.schema_name)
        self.assertEqual(self.api.SimpleService, type_info.py_type)
        self.assertEqual(1, len(type_info.attributes))
        self.assertIn(TypeAttribute.METHODS, type_info.attributes)
        methods = type_info.attributes[TypeAttribute.METHODS]
        self.assertEqual(1, len(methods))

        # getSimpleStruct
        memberInfo = methods[0]
        self.assertEqual("getSimpleStruct", memberInfo.schema_name)
        self._checkSimpleStruct(memberInfo.type_info)
        self.assertEqual(2, len(memberInfo.attributes))
        self.assertIn(MemberAttribute.CLIENT_METHOD_NAME, memberInfo.attributes)
        self.assertEqual("get_simple_struct", memberInfo.attributes[MemberAttribute.CLIENT_METHOD_NAME])
        self.assertIn(MemberAttribute.REQUEST_TYPE, memberInfo.attributes)
        self._checkSimpleUnion(memberInfo.attributes[MemberAttribute.REQUEST_TYPE])

    BLOB_NAME_WITH_OPTIONALS = os.path.join(getApiDir(os.path.dirname(__file__)),
                                            "with_type_info_code_optionals.blob")
    BLOB_NAME_WITHOUT_OPTIONALS = os.path.join(getApiDir(os.path.dirname(__file__)),
                                               "with_type_info_code.blob")
