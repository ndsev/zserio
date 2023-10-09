import os
import apsw

import SqlWithoutRowIdTables

class SimpleWithoutRowIdTableTest(SqlWithoutRowIdTables.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.SimpleWithoutRowIdDb.from_file(self.dbFileName)

    def tearDown(self):
        self._database.close()

    def testRowIdColumn(self):
        self._database.create_schema()
        self.assertFalse(self._isColumnInTable("rowid", self.TABLE_NAME))

    def testCreateOrdinaryRowIdTable(self):
        testTable = self._database.simple_without_row_id_table
        testTable.create_ordinary_rowid_table()
        self.assertTrue(self._isColumnInTable("rowid", self.TABLE_NAME))

    def testCheckWithoutRowIdTableNamesBlackList(self):
        withoutRowIdTableNamesBlackList = [self.TABLE_NAME]
        self._database.create_schema(withoutRowIdTableNamesBlackList)
        self.assertTrue(self._isColumnInTable("rowid", self.TABLE_NAME))

    def _isColumnInTable(self, columnName, tableName):
        sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0"

        # try select to check if hidden column exists
        try:
            self._database.connection.cursor().execute(sqlQuery)
            return True
        except apsw.SQLError:
            return False

    TABLE_NAME = "simpleWithoutRowIdTable"
