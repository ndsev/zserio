import unittest
import os

from testutils import getZserioApi, getApiDir

class MultipleWithSameNameWithSameNameTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "explicit_parameters.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                               "multiple_with_same_name_test.sqlite")

        test_api = cls.api.multiple_with_same_name
        class MultipleWithSameNameTableParameterProvider(test_api.MultipleWithSameNameTable.IParameterProvider):
            def __init__(self):
                self._param1 = PARAM1
                self._param2 = PARAM2

            def param1(self, _row):
                return self._param1

            def param2(self, _row):
                return self._param2

        cls.MultipleWithSameNameTableParameterProvider = MultipleWithSameNameTableParameterProvider

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.ExplicitParametersDb.from_file(self._fileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.multiple_with_same_name_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.multiple_with_same_name_table

        writtenRows = self._createMultipleWithSameNameTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.MultipleWithSameNameTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkMultipleWithSameNameTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.multiple_with_same_name_table

        writtenRows = self._createMultipleWithSameNameTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.MultipleWithSameNameTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkMultipleWithSameNameTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.multiple_with_same_name_table

        writtenRows = self._createMultipleWithSameNameTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createMultipleWithSameNameTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.MultipleWithSameNameTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkMultipleWithSameNameTableRow(updateRow, readRow)

    def _createMultipleWithSameNameTableRows(self):
        rows = []
        for rowId in range(NUM_ROWS):
            rows.append(self._createMultipleWithSameNameTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createMultipleWithSameNameTableRow(self, rowId, name):
        parameterized1 = self.api.multiple_with_same_name.Parameterized1(PARAM1, rowId * 10)
        parameterized2 = self.api.multiple_with_same_name.Parameterized2(PARAM2, rowId * 1.5)

        return (rowId, name, parameterized1, parameterized2)

    def _checkMultipleWithSameNameTableRow(self, row1, row2):
        self.assertEqual(row1, row2)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == TABLE_NAME:
                return True

        return False

TABLE_NAME = "multipleWithSameNameTable"
NUM_ROWS = 5
PARAM1 = 100
PARAM2 = 10.0
