import os

import ExplicitParameters


class MultipleExplicitParamsTest(ExplicitParameters.TestCaseWithDb):
    @classmethod
    def setUpClass(cls):
        super(MultipleExplicitParamsTest, cls).setUpClass()

        test_api = cls.api.multiple_explicit_params

        class MultipleParamsTableParameterProvider(test_api.MultipleParamsTable.IParameterProvider):
            @staticmethod
            def count(_row):
                return MULTIPLE_PARAMS_TABLE_COUNT

            @staticmethod
            def count1(_row):
                return MULTIPLE_PARAMS_TABLE_COUNT1

            @staticmethod
            def count2(_row):
                return MULTIPLE_PARAMS_TABLE_COUNT2

        cls.MultipleParamsTableParameterProvider = MultipleParamsTableParameterProvider

    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.ExplicitParametersDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.multiple_params_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.multiple_params_table

        writtenRows = self._createMultipleParamsTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.MultipleParamsTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkMultipleParamsTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.multiple_params_table

        writtenRows = self._createMultipleParamsTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.MultipleParamsTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkMultipleParamsTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.multiple_params_table

        writtenRows = self._createMultipleParamsTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createMultipleParamsTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.MultipleParamsTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkMultipleParamsTableRow(updateRow, readRow)

    def _createMultipleParamsTableRows(self):
        rows = []
        for rowId in range(NUM_MULTIPLE_PARAMS_TABLE_ROWS):
            rows.append(self._createMultipleParamsTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createMultipleParamsTableRow(self, rowId, name):
        values8 = [rowId for i in range(MULTIPLE_PARAMS_TABLE_COUNT1)]
        values16 = [rowId for i in range(MULTIPLE_PARAMS_TABLE_COUNT2)]
        testBlob1 = self.api.multiple_explicit_params.TestBlob(len(values8), len(values16), values8, values16)

        values8 = [rowId + 1 for i in range(MULTIPLE_PARAMS_TABLE_COUNT)]
        values16 = [rowId + 1 for i in range(MULTIPLE_PARAMS_TABLE_COUNT)]
        testBlob2 = self.api.multiple_explicit_params.TestBlob(len(values8), len(values16), values8, values16)

        values8 = [rowId + 2 for i in range(MULTIPLE_PARAMS_TABLE_COUNT1)]
        values16 = [rowId + 2 for i in range(MULTIPLE_PARAMS_TABLE_COUNT1)]
        testBlob3 = self.api.multiple_explicit_params.TestBlob(len(values8), len(values16), values8, values16)

        return (rowId, name, testBlob1, testBlob2, testBlob3)

    def _checkMultipleParamsTableRow(self, row1, row2):
        self.assertEqual(row1, row2)

        # check reused explicit count1 parameter
        blob1 = row2[2]
        blob3 = row2[4]
        self.assertEqual(blob1.count8, blob3.count8)
        self.assertEqual(blob1.count8, blob3.count16)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == TABLE_NAME:
                return True

        return False


TABLE_NAME = "multipleParamsTable"
NUM_MULTIPLE_PARAMS_TABLE_ROWS = 5
MULTIPLE_PARAMS_TABLE_COUNT1 = 10
MULTIPLE_PARAMS_TABLE_COUNT2 = 11
MULTIPLE_PARAMS_TABLE_COUNT = 12
