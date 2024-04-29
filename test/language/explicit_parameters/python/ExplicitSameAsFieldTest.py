import os

import ExplicitParameters


class ExplicitSameAsFieldTest(ExplicitParameters.TestCaseWithDb):
    @classmethod
    def setUpClass(cls):
        super(ExplicitSameAsFieldTest, cls).setUpClass()

        test_api = cls.api.explicit_same_as_field

        class SameAsFieldTableParameterProvider(test_api.SameAsFieldTable.IParameterProvider):
            @staticmethod
            def count(_row):
                return SAME_AS_FIELD_TABLE_COUNT_EXPLICIT

        cls.SameAsFieldTableParameterProvider = SameAsFieldTableParameterProvider

    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.ExplicitParametersDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.same_as_field_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.same_as_field_table

        writtenRows = self._createSameAsFieldTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.SameAsFieldTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkSameAsFieldTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.same_as_field_table

        writtenRows = self._createSameAsFieldTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.SameAsFieldTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkSameAsFieldTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.same_as_field_table

        writtenRows = self._createSameAsFieldTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createSameAsFieldTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.SameAsFieldTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkSameAsFieldTableRow(updateRow, readRow)

    def _createSameAsFieldTableRows(self):
        rows = []
        for rowId in range(NUM_SIMPLE_PARAM_TABLE_ROWS):
            rows.append(self._createSameAsFieldTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createSameAsFieldTableRow(self, rowId, name):
        values = [rowId for i in range(SAME_AS_FIELD_TABLE_COUNT)]
        testBlob = self.api.explicit_same_as_field.TestBlob(len(values), values)

        valuesExplicit = [rowId + 1 for i in range(SAME_AS_FIELD_TABLE_COUNT_EXPLICIT)]
        testBlobExplicit = self.api.explicit_same_as_field.TestBlob(len(valuesExplicit), valuesExplicit)

        return (rowId, name, SAME_AS_FIELD_TABLE_COUNT, testBlob, testBlobExplicit)

    def _checkSameAsFieldTableRow(self, row1, row2):
        self.assertEqual(row1, row2)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == TABLE_NAME:
                return True

        return False


TABLE_NAME = "sameAsFieldTable"
NUM_SIMPLE_PARAM_TABLE_ROWS = 5
SAME_AS_FIELD_TABLE_COUNT = 10
SAME_AS_FIELD_TABLE_COUNT_EXPLICIT = 11
