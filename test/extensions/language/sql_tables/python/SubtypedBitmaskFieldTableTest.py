import os

import SqlTables


class SubtypedBitmaskFieldTableTest(SqlTables.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.TestDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testReadWithoutCondition(self):
        testTable = self._database.subtyped_bitmask_field_table

        writtenRows = self._createRows()
        testTable.write(writtenRows)

        readRows = testTable.read()
        numReadRows = 0
        for readRow in readRows:
            self.assertEqual(writtenRows[numReadRows], readRow)
            numReadRows += 1
        self.assertTrue(len(writtenRows), numReadRows)

    def _createRows(self):
        rows = []
        for i in range(self.NUM_ROWS):
            rows.append(self._createRow(i))

        return rows

    def _createRow(self, i):
        TestBitmask = self.api.subtyped_bitmask_field_table.TestBitmask
        return (
            i,
            (
                TestBitmask.Values.ONE
                if i % 3 == 0
                else (TestBitmask.Values.TWO if i % 3 == 1 else TestBitmask.Values.THREE)
            ),
        )

    NUM_ROWS = 5
