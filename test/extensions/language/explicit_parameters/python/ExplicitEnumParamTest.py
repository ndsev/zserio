import os

import ExplicitParameters


class ExplicitEnumParamTest(ExplicitParameters.TestCaseWithDb):
    @classmethod
    def setUpClass(cls):
        super(ExplicitEnumParamTest, cls).setUpClass()

        test_api = cls.api.explicit_enum_param

        class EnumParamTableParameterProvider(test_api.EnumParamTable.IParameterProvider):
            @staticmethod
            def count1(_row):
                return test_api.TestEnum.TEN

            @staticmethod
            def count2(_row):
                return test_api.TestEnum.ELEVEN

        cls.EnumParamTableParameterProvider = EnumParamTableParameterProvider

    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.ExplicitParametersDb.from_file(self.dbFileName)
        self._database.create_schema()

        self._enumParamTableCount1 = self.api.explicit_enum_param.TestEnum.TEN
        self._enumParamTableCount2 = self.api.explicit_enum_param.TestEnum.ELEVEN

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.enum_param_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.enum_param_table

        writtenRows = self._createEnumParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.EnumParamTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkEnumParamTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.enum_param_table

        writtenRows = self._createEnumParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.EnumParamTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkEnumParamTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.enum_param_table

        writtenRows = self._createEnumParamTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createEnumParamTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.EnumParamTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkEnumParamTableRow(updateRow, readRow)

    def _createEnumParamTableRows(self):
        rows = []
        for rowId in range(NUM_ENUM_PARAM_TABLE_ROWS):
            rows.append(self._createEnumParamTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createEnumParamTableRow(self, rowId, name):
        values1 = [rowId for i in range(self._enumParamTableCount1.value)]
        testBlob1 = self.api.explicit_enum_param.TestBlob(self._enumParamTableCount1, values1)

        values2 = [rowId + 1 for i in range(self._enumParamTableCount2.value)]
        testBlob2 = self.api.explicit_enum_param.TestBlob(self._enumParamTableCount2, values2)

        values3 = [rowId + 2 for i in range(self._enumParamTableCount1.value)]
        testBlob3 = self.api.explicit_enum_param.TestBlob(self._enumParamTableCount1, values3)

        return (rowId, name, testBlob1, testBlob2, testBlob3)

    def _checkEnumParamTableRow(self, row1, row2):
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


TABLE_NAME = "enumParamTable"
NUM_ENUM_PARAM_TABLE_ROWS = 5
