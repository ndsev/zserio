import unittest
import os

from testutils import getZserioApi, getApiDir

class MultiplePkTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_tables.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "multiple_pk_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.fromFile(self._fileName)
        self._database.createSchema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.getMultiplePkTable()
        testTable.deleteTable()
        self.assertFalse(self._isTableInDb())

        testTable.createTable()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.getMultiplePkTable()

        writtenRows = self._createMultiplePkTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.getMultiplePkTable()

        writtenRows = self._createMultiplePkTableRows()
        testTable.write(writtenRows)

        condition = "name='Name1'"
        readRows = testTable.read(condition)
        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.getMultiplePkTable()

        writtenRows = self._createMultiplePkTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createMultiplePkTableRow(updateRowId, "UpdatedName")
        updateCondition = "blobId=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        readRows = testTable.read(updateCondition)
        for readRow in readRows:
            self.assertEqual(updateRow, readRow)

    def _createMultiplePkTableRows(self):
        rows = []
        for blobId in range(self.NUM_MULTIPLE_PK_TABLE_ROWS):
            rows.append(self._createMultiplePkTableRow(blobId, "Name" + str(blobId)))

        return rows

    @staticmethod
    def _createMultiplePkTableRow(blobId, name):
        return (blobId, 10, name)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + self.TABLE_NAME + "'"
        for row in self._database.executeQuery(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    TABLE_NAME = "multiplePkTable"

    NUM_MULTIPLE_PK_TABLE_ROWS = 5
