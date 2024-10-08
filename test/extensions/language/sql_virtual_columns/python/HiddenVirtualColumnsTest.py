import os
import apsw

import SqlVirtualColumns


class HiddenVirtualColumnsTest(SqlVirtualColumns.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.HiddenVirtualColumnsDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.hidden_virtual_columns_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.hidden_virtual_columns_table

        writtenRows = self._createHiddenVirtualColumnsTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.hidden_virtual_columns_table

        writtenRows = self._createHiddenVirtualColumnsTableRows()
        testTable.write(writtenRows)

        condition = "searchTags='Search Tags1'"
        readRows = testTable.read(condition)

        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.hidden_virtual_columns_table

        writtenRows = self._createHiddenVirtualColumnsTableRows()
        testTable.write(writtenRows)

        updateDocId = 1
        updateRow = self._createHiddenVirtualColumnsTableRow(updateDocId, "Updated Search Tags")
        updateCondition = "docId='" + str(updateDocId) + "'"
        testTable.update(updateRow, updateCondition)

        readRows = testTable.read(updateCondition)
        for readRow in readRows:
            self.assertEqual(updateRow, readRow)

    def testVirtualColumns(self):
        self.assertTrue(self._isHiddenVirtualColumnInTable("docId"))
        self.assertTrue(self._isHiddenVirtualColumnInTable("languageCode"))

    def _createHiddenVirtualColumnsTableRows(self):
        rows = []
        for docId in range(self.NUM_TABLE_ROWS):
            rows.append(self._createHiddenVirtualColumnsTableRow(docId, "Search Tags" + str(docId)))

        return rows

    def _createHiddenVirtualColumnsTableRow(self, docId, searchTags):
        return (docId, self.LANGUAGE_CODE_VALUE, searchTags, self.FREQUENCY_VALUE)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + self.TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    def _isHiddenVirtualColumnInTable(self, columnName):
        sqlQuery = "SELECT " + columnName + " FROM " + self.TABLE_NAME + " LIMIT 0"

        # try select to check if hidden column exists
        try:
            self._database.connection.cursor().execute(sqlQuery)
            return True
        except apsw.SQLError:
            return False

    TABLE_NAME = "hiddenVirtualColumnsTable"

    NUM_TABLE_ROWS = 5

    LANGUAGE_CODE_VALUE = 1
    FREQUENCY_VALUE = 0xDEAD
