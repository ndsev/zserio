import unittest
import os

from testutils import getZserioApi, getApiDir

class DbWithRelocationTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_databases.zs").db_with_relocation
        cls._europeDbFileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                             "db_with_relocation_test_europe.sqlite")
        cls._americaDbFileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                              "db_with_relocation_test_america.sqlite")

    def setUp(self):
        if os.path.exists(self._europeDbFileName):
            os.remove(self._europeDbFileName)
        if os.path.exists(self._americaDbFileName):
            os.remove(self._americaDbFileName)

        self._europeDb = self.api.EuropeDb.from_file(self._europeDbFileName)
        self._europeDb.create_schema()

        tableToDbFileNameRelocationMap = {self.RELOCATED_SLOVAKIA_TABLE_NAME : self._europeDbFileName,
                                          self.RELOCATED_CZECHIA_TABLE_NAME : self._europeDbFileName}
        self._americaDb = self.api.AmericaDb.from_file(self._americaDbFileName, tableToDbFileNameRelocationMap)
        self._americaDb.create_schema()

    def tearDown(self):
        self._americaDb.close()
        self._europeDb.close()

    def testTableGetters(self):
        germanyTable = self._europeDb.germany
        self.assertNotEqual(None, germanyTable)

        usaTable = self._americaDb.usa
        self.assertNotEqual(None, usaTable)

        canadaTable = self._americaDb.canada
        self.assertNotEqual(None, canadaTable)

        slovakiaTable = self._americaDb.slovakia
        self.assertNotEqual(None, slovakiaTable)

        czechiaTable = self._americaDb.czechia
        self.assertNotEqual(None, czechiaTable)

    def testRelocatedSlovakiaTable(self):
        # check that americaDb does not contain relocated table
        self.assertFalse(self._isTableInDb(self._americaDb, self.RELOCATED_SLOVAKIA_TABLE_NAME))

        # check that europeDb does contain relocated table
        self.assertTrue(self._isTableInDb(self._europeDb, self.RELOCATED_SLOVAKIA_TABLE_NAME))

        # write to relocated table
        updateTileId = 1
        writtenRows = [(updateTileId, self.api.Tile(ord('a'), ord('A')))]
        relocatedTable = self._americaDb.slovakia
        relocatedTable.write(writtenRows)

        # update it
        updatedRows = [(updateTileId, self.api.Tile(ord('b'), ord('B')))]
        updateCondition = "tileId=" + str(updateTileId)
        relocatedTable.update(updatedRows[0], updateCondition)

        # read it back
        readRows = relocatedTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(updatedRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(updatedRows), numReadRows)

    def testRelocatedCzechiaTable(self):
        # check that americaDb does not contain relocated table
        self.assertFalse(self._isTableInDb(self._americaDb, self.RELOCATED_CZECHIA_TABLE_NAME))

        # check that europeDb does contain relocated table
        self.assertTrue(self._isTableInDb(self._europeDb, self.RELOCATED_CZECHIA_TABLE_NAME))

        # write to relocated table
        updateTileId = 1
        writtenRows = [(updateTileId, self.api.Tile(ord('c'), ord('C')))]
        relocatedTable = self._americaDb.czechia
        relocatedTable.write(writtenRows)

        # update it
        updatedRows = [(updateTileId, self.api.Tile(ord('d'), ord('D')))]
        updateCondition = "tileId=" + str(updateTileId)
        relocatedTable.update(updatedRows[0], updateCondition)

        # read it back
        readRows = relocatedTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(updatedRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(updatedRows), numReadRows)

    def testAttachedDatabases(self):
        attachedDatabaseNames = ["main",
                                 "AmericaDb_" + self.RELOCATED_SLOVAKIA_TABLE_NAME,
                                 "AmericaDb_" + self.RELOCATED_CZECHIA_TABLE_NAME]
        sqlQuery = "PRAGMA database_list"
        rows = self._americaDb.connection.cursor().execute(sqlQuery)
        for row in rows:
            self.assertIn(row[1], attachedDatabaseNames)
            attachedDatabaseNames.remove(row[1])
        self.assertEqual(1, len(attachedDatabaseNames))
        self.assertNotIn("main", attachedDatabaseNames)

    @staticmethod
    def _isTableInDb(database, tableName):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'"
        rows = database.connection.cursor().execute(sqlQuery)
        for row in rows:
            if len(row) == 1 and row[0] == tableName:
                return True

        return False

    RELOCATED_SLOVAKIA_TABLE_NAME = "slovakia"
    RELOCATED_CZECHIA_TABLE_NAME = "czechia"
