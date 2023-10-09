import unittest

from testutils import getZserioApi, getTestCaseName

class WarningsTestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "array_types_warning.zs",
                               expectedWarnings=12, errorOutputDict=cls.warnings)

class TestCase(WarningsTestCase):
    @classmethod
    def setUpClass(cls):
        super(TestCase, cls).setUpClass()
        cls.api = getattr(cls.api, getTestCaseName(cls.__name__))
