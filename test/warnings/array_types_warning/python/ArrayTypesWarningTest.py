import ArrayTypesWarning

from testutils import assertWarningsPresent

class ArrayTypesWarningTest(ArrayTypesWarning.WarningsTestCase):
    def testPackedArrayChoiceHasNoPackableField(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_choice_has_no_packable_field.zs:40:12: "
                "Keyword 'packed' doesn't have any effect. "
                "'ChoiceWithoutPackableField' doesn't contain any packable field."
            ]
        )

    def testPackedArrayStructHasNoPackableField(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_struct_has_no_packable_field.zs:48:12: "
                "Keyword 'packed' doesn't have any effect. "
                "'StructWithoutPackable' doesn't contain any packable field."
            ]
        )

    def testPackedArrayTemplateHasNoPackableField(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_template_has_no_packable_field.zs:22:13: "
                "    In instantiation of 'Template' required from here",
                "packed_array_template_has_no_packable_field.zs:5:12: "
                "Keyword 'packed' doesn't have any effect. 'string' is not packable element type."
            ]
        )

        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_template_has_no_packable_field.zs:24:13: "
                "    In instantiation of 'Template' required from here",
                "packed_array_template_has_no_packable_field.zs:5:12: "
                "Keyword 'packed' doesn't have any effect. 'Unpackable' doesn't contain any packable field."
            ]
        )

    def testPackedArrayUnionHasNoPackableField(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_union_has_no_packable_field.zs:25:12: "
                "Union 'UnionWithoutPackableField' doesn't contain any packable field."
            ]
        )

    def testPackedArrayUnpackableBoolElement(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_unpackable_bool_element.zs:23:12: "
                "Keyword 'packed' doesn't have any effect. 'bool' is not packable element type."
            ]
        )

    def testPackedArrayUnpackableBytesElement(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_unpackable_bytes_element.zs:6:12: "
                "Keyword 'packed' doesn't have any effect. 'bytes' is not packable element type."
            ]
        )

    def testPackedArrayUnpackableExternElement(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_unpackable_extern_element.zs:6:12: "
                "Keyword 'packed' doesn't have any effect. 'extern' is not packable element type."
            ]
        )

    def testPackedArrayUnpackableFloatElement(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_unpackable_float_element.zs:6:12: "
                "Keyword 'packed' doesn't have any effect. 'float64' is not packable element type."
            ]
        )

    def testPackedArrayUnpackableStringElement(self):
        assertWarningsPresent(self,
            "array_types_warning.zs",
            [
                "packed_array_unpackable_string_element.zs:6:12: "
                "Keyword 'packed' doesn't have any effect. 'string' is not packable element type."
            ]
        )
