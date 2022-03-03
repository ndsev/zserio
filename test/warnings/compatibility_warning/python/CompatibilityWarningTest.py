import unittest

from testutils import getZserioApi, assertWarningsPresent

class CompatibilityWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "compatibility_warning/root_with_diff_compatibility_warning.zs",
                               expectedWarnings=1, errorOutputDict=cls.warnings,
                               extraArgs=["-withoutCrossExtensionCheck"])
        cls.api = getZserioApi(__file__, "compatibility_warning/root_without_compatibility_warning.zs",
                               expectedWarnings=1, errorOutputDict=cls.warnings,
                               extraArgs=["-withoutCrossExtensionCheck"])

    def testRootWithDiffCompatibility(self):
        assertWarningsPresent(self,
            "compatibility_warning/root_with_diff_compatibility_warning.zs",
            [
                "subpackage.zs:1:30: "
                "Package compatibility version '2.4.2' doesn't match to '2.5.0' specified in root package!"
            ]
        )

    def testRootWithoutCompatibility(self):
        assertWarningsPresent(self,
            "compatibility_warning/root_without_compatibility_warning.zs",
            [
                "subpackage.zs:1:30: "
                "Package specifies compatibility version '2.4.2' while root package specifies nothing"
            ]
        )
