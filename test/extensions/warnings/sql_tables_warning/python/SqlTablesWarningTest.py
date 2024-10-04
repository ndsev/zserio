import unittest

from testutils import getZserioApi


class SqlTablesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "sql_tables_warning.zs", expectedWarnings=7, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
