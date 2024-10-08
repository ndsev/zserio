import os

import SqlTables


class BlobParamTableTest(SqlTables.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.TestDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.blob_param_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.blob_param_table

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1

        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.blob_param_table

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        condition = "name='Name1'"
        readRows = testTable.read(condition)
        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.blob_param_table

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
        testTable = self._database.blob_param_table

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
        parameters = self.api.blob_param_table.Parameters(self.PARAMETERS_COUNT)
        array = list(range(self.PARAMETERS_COUNT))
        blob = self.api.blob_param_table.ParameterizedBlob(parameters, array)

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
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    TABLE_NAME = "blobParamTable"

    PARAMETERS_COUNT = 10
    NUM_BLOB_PARAM_TABLE_ROWS = 5
