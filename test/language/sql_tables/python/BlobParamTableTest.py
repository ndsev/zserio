import unittest
import os

from testutils import getZserioApi, getApiDir

class BlobParamTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_tables.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "blob_param_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.fromFile(self._fileName)
        self._database.createSchema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.getBlobParamTable()
        testTable.deleteTable()
        self.assertFalse(self._isTableInDb())

        testTable.createTable()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.getBlobParamTable()

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1

        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.getBlobParamTable()

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        condition = "name='Name1'"
        readRows = testTable.read(condition)
        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.getBlobParamTable()

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createBlobParamTableRow(updateRowId, "UpdatedName")
        updateCondition = "blobId=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        readRows = testTable.read(updateCondition)
        for readRow in readRows:
            self.assertEqual(updateRow, readRow)

    def testNullValues(self):
        testTable = self._database.getBlobParamTable()

        writtenRows = self._createBlobParamTableRowsWithNullValues()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1

        self.assertTrue(len(writtenRows), numReadRows)

    def _createBlobParamTableRows(self):
        rows = []
        for blobId in range(self.NUM_BLOB_PARAM_TABLE_ROWS):
            rows.append(self._createBlobParamTableRow(blobId, "Name" + str(blobId)))

        return rows

    def _createBlobParamTableRow(self, blobId, name):
        parameters = self.api.blob_param_table.Parameters.fromFields(self.PARAMETERS_COUNT)
        array = [i for i in range(self.PARAMETERS_COUNT)]
        blob = self.api.blob_param_table.ParameterizedBlob.fromFields(parameters, array)

        return (blobId, name, parameters, blob)

    def _createBlobParamTableRowsWithNullValues(self):
        rows = []
        for blobId in range(self.NUM_BLOB_PARAM_TABLE_ROWS):
            rows.append(self._createBlobParamTableRowWithNullValues(blobId))

        return rows

    @staticmethod
    def _createBlobParamTableRowWithNullValues(blobId):
        return (blobId, None, None, None)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + self.TABLE_NAME + "'"
        for row in self._database.connection().cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    TABLE_NAME = "blobParamTable"

    PARAMETERS_COUNT = 10
    NUM_BLOB_PARAM_TABLE_ROWS = 5
