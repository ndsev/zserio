import os

import ExplicitParameters


class ExplicitBlobParamTest(ExplicitParameters.TestCaseWithDb):
    @classmethod
    def setUpClass(cls):
        super(ExplicitBlobParamTest, cls).setUpClass()

        test_api = cls.api.explicit_blob_param

        class BlobParamTableParameterProvider(test_api.BlobParamTable.IParameterProvider):
            def __init__(self):
                self._headerParam = test_api.Header(BLOB_PARAM_TABLE_HEADER_COUNT)
                self._blob = test_api.Header(BLOB_PARAM_TABLE_BLOB_COUNT)

            def header_param(self, _row):
                return self._headerParam

            def blob(self, _row):
                return self._blob

        cls.BlobParamTableParameterProvider = BlobParamTableParameterProvider

    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.ExplicitParametersDb.from_file(self.dbFileName)
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

        parameterProvider = self.BlobParamTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkBlobParamTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.blob_param_table

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.BlobParamTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkBlobParamTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.blob_param_table

        writtenRows = self._createBlobParamTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createBlobParamTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.BlobParamTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkBlobParamTableRow(updateRow, readRow)

    def _createBlobParamTableRows(self):
        rows = []
        for rowId in range(NUM_BLOB_PARAM_TABLE_ROWS):
            rows.append(self._createBlobParamTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createBlobParamTableRow(self, rowId, name):
        header = self.api.explicit_blob_param.Header(BLOB_PARAM_TABLE_HEADER_COUNT)

        values1 = [rowId for i in range(BLOB_PARAM_TABLE_HEADER_COUNT)]
        testBlob1 = self.api.explicit_blob_param.TestBlob(header, values1)

        blob = self.api.explicit_blob_param.Header(BLOB_PARAM_TABLE_BLOB_COUNT)
        values2 = [rowId + 1 for i in range(BLOB_PARAM_TABLE_BLOB_COUNT)]
        testBlob2 = self.api.explicit_blob_param.TestBlob(blob, values2)

        values3 = [rowId + 2 for i in range(BLOB_PARAM_TABLE_HEADER_COUNT)]
        testBlob3 = self.api.explicit_blob_param.TestBlob(header, values3)

        return (rowId, name, testBlob1, testBlob2, testBlob3)

    def _checkBlobParamTableRow(self, row1, row2):
        self.assertEqual(row1, row2)

        # check reused explicit header parameter
        blob1 = row2[2]
        blob3 = row2[4]
        self.assertEqual(blob1.blob, blob3.blob)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == TABLE_NAME:
                return True

        return False


TABLE_NAME = "blobParamTable"
NUM_BLOB_PARAM_TABLE_ROWS = 5
BLOB_PARAM_TABLE_HEADER_COUNT = 10
BLOB_PARAM_TABLE_BLOB_COUNT = 11
