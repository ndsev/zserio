import DefaultValues

from compoundutils import writeReadTest
from zserio.bitbuffer import BitBuffer


class ExtendedDefaultValuesTest(DefaultValues.TestCase):
    def testNoDefaultU32Field(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(0, data.no_default_u32_field)

    def testNoDefaultStringField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.no_default_string_field == "")

    def testExtendedDefaultBoolField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_default_bool_field_present())
        self.assertEqual(True, data.extended_default_bool_field)

    def testExtendedDefaultStringField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_default_string_field_present())
        self.assertEqual("default", data.extended_default_string_field)

    def testExtendedOptionalDefaultFloatField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_optional_default_float_field_present())
        self.assertEqual(True, data.is_extended_optional_default_float_field_set())
        self.assertEqual(1.234, data.extended_optional_default_float_field)

    def testExtendedOptionalDefaultStringField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_optional_default_string_field_present())
        self.assertEqual(True, data.is_extended_optional_default_string_field_set())
        self.assertEqual("default", data.extended_optional_default_string_field)

    def testExtendedNoDefaultU32Field(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_no_default_u32_field_present())
        self.assertEqual(0, data.extended_no_default_u32_field)

    def testExtendedNoDefaultExternField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_no_default_extern_field_present())
        self.assertIsNone(data.extended_no_default_extern_field)

    def testExtendedOptionalNoDefaultU32Field(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_optional_no_default_u32_field_present())
        self.assertEqual(False, data.is_extended_optional_no_default_u32_field_set())

    def testExtendedOptionalNoDefaultBytesField(self):
        data = self.api.ExtendedDefaultValues()
        self.assertEqual(True, data.is_extended_optional_no_default_bytes_field_present())
        self.assertEqual(False, data.is_extended_optional_no_default_bytes_field_set())

    def testwriteRead(self):
        data = self.api.ExtendedDefaultValues()
        data.extended_no_default_extern_field = BitBuffer(bytes([0xDE, 0xAD]), 2)
        # default value=1.234 will not work due to #384
        data.extended_optional_default_float_field = 50
        writeReadTest(self.api.ExtendedDefaultValues, data)
