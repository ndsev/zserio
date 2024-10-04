import unittest

from testutils import getZserioApi


class SqlWithoutRowIdTablesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__, "sql_without_rowid_tables_warning.zs", expectedWarnings=1, errorOutputDict=cls.warnings
        )

    def testDummy(self):
        pass
