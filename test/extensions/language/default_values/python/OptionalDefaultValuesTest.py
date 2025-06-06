import DefaultValues

from compoundutils import writeReadTest


class OptionalDefaultValuesTest(DefaultValues.TestCase):
    def testOptionalNoDefaultBoolField(self):
        data = self.api.OptionalDefaultValues()
        self.assertEqual(False, data.is_optional_no_default_bool_field_set())

    def testOptionalNoDefaultString(self):
        data = self.api.OptionalDefaultValues()
        self.assertEqual(False, data.is_optional_no_default_string_field_set())

    def testOptionalDefaultU32(self):
        data = self.api.OptionalDefaultValues()
        self.assertEqual(True, data.is_optional_default_u32_field_set())
        self.assertEqual(13, data.optional_default_u32_field)

    def testOptionalDefaultF64(self):
        data = self.api.OptionalDefaultValues()
        self.assertEqual(True, data.is_optional_default_f64_field_set())
        self.assertEqual(1.234, data.optional_default_f64_field)

    def testOptionalDefaultString(self):
        data = self.api.OptionalDefaultValues()
        self.assertEqual(True, data.is_optional_default_string_field_set())
        self.assertEqual("default", data.optional_default_string_field)

    def testwriteRead(self):
        data = self.api.OptionalDefaultValues()
        writeReadTest(self.api.OptionalDefaultValues, data)
