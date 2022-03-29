import unittest

from testutils import getZserioApi, assertWarningsPresent

class SqlTablesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "sql_tables_warning.zs",
                               expectedWarnings=7, errorOutputDict=cls.warnings)

    def testBadOrderedPrimaryKey(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "bad_ordered_primary_key_warning.zs:9:9: "
                "Primary key column 'classId' is in bad order in sql table 'BadOrderedPrimaryKeyTable'."
            ]
        )

    def testDuplicatedPrimaryKey(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "duplicated_primary_key_warning.zs:6:33: "
                "Duplicated primary key column 'classId' in sql table 'DuplicatedPrimaryKeyTable'."
            ]
        )

    def testMultiplePrimaryKeys(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "multiple_primary_keys_warning.zs:9:9: "
                "Multiple primary keys in sql table 'MultiplePrimaryKeysTable'."
            ]
        )

    def testNoPrimaryKey(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "no_primary_key_warning.zs:3:11: "
                "No primary key in sql table 'NoPrimaryKeyTable'."
            ]
        )

    def testNotFirstPrimaryKey(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "not_first_primary_key_warning.zs:6:29: "
                "Primary key column 'classId' is not the first one in sql table 'NotFirstPrimaryKeyTable'."
            ]
        )

    def testNotNullPrimaryKey(self):
        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "not_null_primary_key_warning.zs:5:17: "
                "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable1'."
            ]
        )

        assertWarningsPresent(self,
            "sql_tables_warning.zs",
            [
                "not_null_primary_key_warning.zs:14:17: "
                "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable2'."
            ]
        )
