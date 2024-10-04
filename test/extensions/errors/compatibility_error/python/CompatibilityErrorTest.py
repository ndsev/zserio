import unittest

from testutils import compileErroneousZserio, assertErrorsPresent


class CompatibilityErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "packed_array_in_template_240_error.zs", cls.errors)
        compileErroneousZserio(__file__, "packed_compound_array_242_error.zs", cls.errors)
        compileErroneousZserio(__file__, "packed_uint32_array_241_error.zs", cls.errors)
        compileErroneousZserio(__file__, "version_less_than_min_supported_error.zs", cls.errors)
        compileErroneousZserio(__file__, "wrong_compatibility_version_format_error.zs", cls.errors)

    def testPackedArrayInTemplate240(self):
        assertErrorsPresent(
            self,
            "packed_array_in_template_240_error.zs",
            [
                ":1:30: Root package requires compatibility with version '2.4.0'!",
                ":7:14: Packed arrays binary encoding has been changed in version '2.5.0'!",
                "Python Generator: Compatibility check failed!",
            ],
        )

    def testPackedCompoundArray242(self):
        assertErrorsPresent(
            self,
            "packed_compound_array_242_error.zs",
            [
                ":1:30: Root package requires compatibility with version '2.4.2'!",
                ":16:25: Packed arrays binary encoding has been changed in version '2.5.0'!",
                "Python Generator: Compatibility check failed!",
            ],
        )

    def testPackedUint32Array241(self):
        assertErrorsPresent(
            self,
            "packed_uint32_array_241_error.zs",
            [
                ":1:30: Root package requires compatibility with version '2.4.1'!",
                ":7:19: Packed arrays binary encoding has been changed in version '2.5.0'!",
                "Python Generator: Compatibility check failed!",
            ],
        )

    def testVersionLessThanMinSupported(self):
        assertErrorsPresent(
            self,
            "version_less_than_min_supported_error.zs",
            [
                ":1:30: Package specifies unsupported compatibility version '2.3.2', "
                + "minimum supported version is '2.4.0'!"
            ],
        )

    def testWrongCompatibilityVersionFormat(self):
        assertErrorsPresent(
            self,
            "wrong_compatibility_version_format_error.zs",
            [
                "wrong_compatibility_version_format_error.zs:2:30: "
                + "Failed to parse version string: '2.5.0-rc1' as a version!"
            ],
        )
