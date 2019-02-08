import unittest
import os
import apsw

from testutils import getZserioApi, getApiDir

class SimpleWithoutRowIdTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_without_rowid_tables.zs").simple_without_rowid_table
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                     "simple_without_rowid_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.SimpleWithoutRowIdDb.fromFile(self._fileName)

    def tearDown(self):
        self._database.close()

    def testRowIdColumn(self):
        self._database.createSchema()
        self.assertFalse(self._isColumnInTable("rowid", self.TABLE_NAME))

    def testCreateOrdinaryRowIdTable(self):
        testTable = self._database.getSimpleWithoutRowIdTable()
        testTable.createOrdinaryRowIdTable()
        self.assertTrue(self._isColumnInTable("rowid", self.TABLE_NAME))

    def testCheckWithoutRowIdTableNamesBlackList(self):
        withoutRowIdTableNamesBlackList = [self.TABLE_NAME]
        self._database.createSchema(withoutRowIdTableNamesBlackList)
        self.assertTrue(self._isColumnInTable("rowid", self.TABLE_NAME))

    def _isColumnInTable(self, columnName, tableName):
        sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0"

        # try select to check if hidden column exists
        try:
            self._database.connection().cursor().execute(sqlQuery)
            return True
        except apsw.SQLError:
            return False

    TABLE_NAME = "simpleWithoutRowIdTable"
