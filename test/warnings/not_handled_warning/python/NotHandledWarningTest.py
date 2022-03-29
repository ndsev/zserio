import unittest

from testutils import getZserioApi, assertWarningsPresent

class NotHandledWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "not_handled_warning.zs",
                               expectedWarnings=2, errorOutputDict=cls.warnings)

    def testNotHandledWhite(self):
        assertWarningsPresent(self,
            "not_handled_warning.zs",
            [
                "not_handled_warning.zs:15:8: "
                "Enumeration value 'WHITE' is not handled in choice 'EnumParamChoice'."
            ]
        )

    def testNotHandledRed(self):
        assertWarningsPresent(self,
            "not_handled_warning.zs",
            [
                "not_handled_warning.zs:15:8: "
                "Enumeration value 'RED' is not handled in choice 'EnumParamChoice'."
            ]
        )
