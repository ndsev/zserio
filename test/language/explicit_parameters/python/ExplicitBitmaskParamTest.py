import unittest
import os

from testutils import getZserioApi, getApiDir

class ExplicitBitmaskParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "explicit_parameters.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "explicit_bitmask_param_test.sqlite")

        test_api = cls.api.explicit_bitmask_param
        class BitmaskParamTableParameterProvider(test_api.BitmaskParamTable.IParameterProvider):
            @staticmethod
            def count1(_row):
                return test_api.TestBitmask.Values.TEN

            @staticmethod
            def count2(_row):
                return test_api.TestBitmask.Values.ELEVEN

        cls.BitmaskParamTableParameterProvider = BitmaskParamTableParameterProvider

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.ExplicitParametersDb.from_file(self._fileName)
        self._database.create_schema()

        self._bitmaskParamTableCount1 = self.api.explicit_bitmask_param.TestBitmask.Values.TEN
        self._bitmaskParamTableCount2 = self.api.explicit_bitmask_param.TestBitmask.Values.ELEVEN

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.bitmask_param_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.bitmask_param_table

        writtenRows = self._createBitmaskParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.BitmaskParamTableParameterProvider()
        readRows = testTable.read(parameterProvider)
        numReadRows = 0
        for readRow in readRows:
            self._checkBitmaskParamTableRow(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.bitmask_param_table

        writtenRows = self._createBitmaskParamTableRows()
        testTable.write(writtenRows)

        parameterProvider = self.BitmaskParamTableParameterProvider()
        condition = "name='Name1'"
        readRows = testTable.read(parameterProvider, condition)
        expectedRowNum = 1
        for readRow in readRows:
            self._checkBitmaskParamTableRow(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.bitmask_param_table

        writtenRows = self._createBitmaskParamTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createBitmaskParamTableRow(updateRowId, "UpdatedName")
        updateCondition = "id=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        parameterProvider = self.BitmaskParamTableParameterProvider()
        readRows = testTable.read(parameterProvider, updateCondition)
        for readRow in readRows:
            self._checkBitmaskParamTableRow(updateRow, readRow)

    def _createBitmaskParamTableRows(self):
        rows = []
        for rowId in range(NUM_BITMASK_PARAM_TABLE_ROWS):
            rows.append(self._createBitmaskParamTableRow(rowId, "Name" + str(rowId)))

        return rows

    def _createBitmaskParamTableRow(self, rowId, name):
        values1 = [rowId for i in range(self._bitmaskParamTableCount1.value)]
        testBlob1 = self.api.explicit_bitmask_param.TestBlob(self._bitmaskParamTableCount1, values1)

        values2 = [rowId + 1 for i in range(self._bitmaskParamTableCount2.value)]
        testBlob2 = self.api.explicit_bitmask_param.TestBlob(self._bitmaskParamTableCount2, values2)

        values3 = [rowId + 2 for i in range(self._bitmaskParamTableCount1.value)]
        testBlob3 = self.api.explicit_bitmask_param.TestBlob(self._bitmaskParamTableCount1, values3)

        return (rowId, name, testBlob1, testBlob2, testBlob3)

    def _checkBitmaskParamTableRow(self, row1, row2):
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

TABLE_NAME = "bitmaskParamTable"
NUM_BITMASK_PARAM_TABLE_ROWS = 5
