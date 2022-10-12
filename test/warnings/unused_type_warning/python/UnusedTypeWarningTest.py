import unittest

from testutils import getZserioApi, assertWarningsPresent

class UnusedTypeWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "unused_type_warning.zs", extraArgs=["-withWarnings", "unused"],
                               expectedWarnings=6, errorOutputDict=cls.warnings)

    def testUnusedEnumeration(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:4:12: "
                "Type 'unused_type_warning.UnusedEnumeration' is not used."
            ]
        )

    def testUnusedSubtype(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:18:15: "
                "Type 'unused_type_warning.UnusedSubtype' is not used."
            ]
        )

    def testUnusedChoice(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:28:8: "
                "Type 'unused_type_warning.UnusedChoice' is not used."
            ]
        )

    def testUnusedUnion(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:48:7: "
                "Type 'unused_type_warning.UnusedUnion' is not used."
            ]
        )

    def testUnusedStructure(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:62:8: "
                "Type 'unused_type_warning.UnusedStructure' is not used."
            ]
        )

    def testUnusedTable(self):
        assertWarningsPresent(self,
            "unused_type_warning.zs",
            [
                "unused_type_warning.zs:76:11: "
                "Type 'unused_type_warning.UnusedTable' is not used."
            ]
        )
