import unittest

from testutils import getZserioApi


class OptionalMembersWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "optional_members_warning.zs", expectedWarnings=25, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
