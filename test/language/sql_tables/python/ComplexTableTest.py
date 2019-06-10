import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class ComplexTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_tables.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "complex_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.fromFile(self._fileName)
        self._database.createSchema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.getComplexTable()
        testTable.deleteTable()
        self.assertFalse(self._isTableInDb())

        testTable.createTable()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.getComplexTable()

        writtenRows = self._createComplexTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read(self._getParameterProvider())
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.getComplexTable()

        writtenRows = self._createComplexTableRows()
        testTable.write(writtenRows)

        condition = "name='Name1'"
        readRows = testTable.read(self._getParameterProvider(), condition)
        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.getComplexTable()

        writtenRows = self._createComplexTableRows()
        testTable.write(writtenRows)

        updateRowId = 3
        updateRow = self._createComplexTableRow(updateRowId, "UpdatedName")
        updateCondition = "blobId=" + str(updateRowId)
        testTable.update(updateRow, updateCondition)

        readRows = testTable.read(self._getParameterProvider(), updateCondition)
        for readRow in readRows:
            self.assertEqual(updateRow, readRow)

    def testNullValues(self):
        testTable = self._database.getComplexTable()

        writtenRows = self._createComplexTableRowsWithNullValues()
        testTable.write(writtenRows)

        readRows = testTable.read(self._getParameterProvider())
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def _getParameterProvider(self):
        class ComplexTableParameterProvider(self.api.complex_table.ComplexTable.IParameterProvider):
            def __init__(self, tableCount):
                self._tableCount = tableCount

            def getCount(self, _row):
                return self._tableCount

        return ComplexTableParameterProvider(self.COMPLEX_TABLE_COUNT)

    def _createComplexTableRows(self):
        rows = []
        for blobId in range(self.NUM_COMPLEX_TABLE_ROWS):
            rows.append(self._createComplexTableRow(blobId, "Name" + str(blobId)))

        return rows

    def _createComplexTableRow(self, blobId, name):
        values = [blobId for i in range(self.COMPLEX_TABLE_COUNT)]
        blob = self.api.complex_table.TestBlob.fromFields(len(values), 0, values, True)

        return (blobId, zserio.limits.INT64_MAX, name, True, 9.9, 5.5, 0x34,
                self.api.complex_table.TestEnum.RED, blob)

    def _createComplexTableRowsWithNullValues(self):
        rows = []
        for blobId in range(self.NUM_COMPLEX_TABLE_ROWS):
            rows.append(self._createComplexTableRowWithNullValues(blobId))

        return rows

    @staticmethod
    def _createComplexTableRowWithNullValues(blobId):
        return (blobId, None, None, None, None, None, None,
                None, None)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + self.TABLE_NAME + "'"
        for row in self._database.connection().cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    TABLE_NAME = "complexTable"

    NUM_COMPLEX_TABLE_ROWS = 5
    COMPLEX_TABLE_COUNT = 10
