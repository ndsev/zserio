import os
import apsw

import SqlVirtualColumns


class SimpleVirtualColumnsTest(SqlVirtualColumns.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.SimpleVirtualColumnsDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testDeleteTable(self):
        self.assertTrue(self._isTableInDb())

        testTable = self._database.simple_virtual_columns_table
        testTable.delete_table()
        self.assertFalse(self._isTableInDb())

        testTable.create_table()
        self.assertTrue(self._isTableInDb())

    def testReadWithoutCondition(self):
        testTable = self._database.simple_virtual_columns_table

        writtenRows = self._createSimpleVirtualColumnsTableRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def testReadWithCondition(self):
        testTable = self._database.simple_virtual_columns_table

        writtenRows = self._createSimpleVirtualColumnsTableRows()
        testTable.write(writtenRows)

        condition = "content='Content1'"
        readRows = testTable.read(condition)

        expectedRowNum = 1
        for readRow in readRows:
            self.assertEqual(writtenRows[expectedRowNum], readRow)

    def testUpdate(self):
        testTable = self._database.simple_virtual_columns_table

        writtenRows = self._createSimpleVirtualColumnsTableRows()
        testTable.write(writtenRows)

        updateContent = "UpdatedContent"
        updateRow = self._createSimpleVirtualColumnsTableRow(updateContent)
        updateCondition = "content='Content3'"
        testTable.update(updateRow, updateCondition)

        readCondition = "content='" + str(updateContent) + "'"
        readRows = testTable.read(readCondition)
        for readRow in readRows:
            self.assertEqual(updateRow, readRow)

    def testVirtualColumns(self):
        self.assertTrue(self._isSimpleVirtualColumnInTable("content"))

    def _createSimpleVirtualColumnsTableRows(self):
        rows = []
        for contentId in range(self.NUM_TABLE_ROWS):
            rows.append(self._createSimpleVirtualColumnsTableRow("Content" + str(contentId)))

        return rows

    @staticmethod
    def _createSimpleVirtualColumnsTableRow(content):
        return (content,)

    def _isTableInDb(self):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + self.TABLE_NAME + "'"
        for row in self._database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == self.TABLE_NAME:
                return True

        return False

    def _isSimpleVirtualColumnInTable(self, columnName):
        sqlQuery = "SELECT " + columnName + " FROM " + self.TABLE_NAME + " LIMIT 0"

        # try select to check if hidden column exists
        try:
            self._database.connection.cursor().execute(sqlQuery)
            return True
        except apsw.SQLError:
            return False

    TABLE_NAME = "simpleVirtualColumnsTable"

    NUM_TABLE_ROWS = 5
