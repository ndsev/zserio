import os

import ExplicitParameters


class ExplicitSimpleParamTest(ExplicitParameters.TestCaseWithDb):
    @classmethod
    def setUpClass(cls):
        super(ExplicitSimpleParamTest, cls).setUpClass()

        test_api = cls.api.explicit_simple_param

        class SimpleParamTableParameterProvider(test_api.SimpleParamTable.IParameterProvider):
            @staticmethod
            def count1(_row):
                return SIMPLE_PARAM_TABLE_COUNT1

            @staticmethod
            def count2(_row):
                return SIMPLE_PARAM_TABLE_COUNT2

        cls.SimpleParamTableParameterProvider = SimpleParamTableParameterProvider

    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.ExplicitParametersDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.simple_param_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.simple_param_table

        writtenRows = self._createSimpleParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.SimpleParamTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkSimpleParamTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.simple_param_table

        writtenRows = self._createSimpleParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.SimpleParamTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkSimpleParamTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.simple_param_table

        writtenRows = self._createSimpleParamTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createSimpleParamTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.SimpleParamTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkSimpleParamTableRow(updateRow, readRow)

    def _createSimpleParamTableRows(self):
        rows = []
        for rowId in range(NUM_SIMPLE_PARAM_TABLE_ROWS):
            rows.append(self._createSimpleParamTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createSimpleParamTableRow(self, rowId, name):
        values1 = [rowId for i in range(SIMPLE_PARAM_TABLE_COUNT1)]
        testBlob1 = self.api.explicit_simple_param.TestBlob(len(values1), values1)

        values2 = [rowId + 1 for i in range(SIMPLE_PARAM_TABLE_COUNT2)]
        testBlob2 = self.api.explicit_simple_param.TestBlob(len(values2), values2)

        values3 = [rowId + 2 for i in range(SIMPLE_PARAM_TABLE_COUNT1)]
        testBlob3 = self.api.explicit_simple_param.TestBlob(len(values3), values3)

        return (rowId, name, testBlob1, testBlob2, testBlob3)

    def _checkSimpleParamTableRow(self, row1, row2):
        self.assertEqual(row1, row2)

        # check reused explicit count1 parameter
        blob1 = row2[2]
        blob3 = row2[4]
        self.assertEqual(blob1.count, blob3.count)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == TABLE_NAME:
                return True

        return False


TABLE_NAME = "simpleParamTable"
NUM_SIMPLE_PARAM_TABLE_ROWS = 5
SIMPLE_PARAM_TABLE_COUNT1 = 10
SIMPLE_PARAM_TABLE_COUNT2 = 11
