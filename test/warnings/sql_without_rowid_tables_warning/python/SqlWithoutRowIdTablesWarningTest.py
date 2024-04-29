import unittest

from testutils import getZserioApi, assertWarningsPresent


class SqlWithoutRowIdTablesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__, "sql_without_rowid_tables_warning.zs", expectedWarnings=1, errorOutputDict=cls.warnings
        )

    def testIntegerPrimaryKey(self):
        assertWarningsPresent(
            self,
            "sql_without_rowid_tables_warning.zs",
            [
                "integer_primary_key_warning.zs:3:11: "
                "Single integer primary key in without rowid table 'WithoutRowIdTable' "
                "brings performance drop."
            ],
        )
