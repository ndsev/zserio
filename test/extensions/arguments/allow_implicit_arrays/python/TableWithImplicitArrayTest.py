import os

import AllowImplicitArrays


class TableWithImplicitArrayTest(AllowImplicitArrays.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.DbWithImplicitArray.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testReadWithoutCondition(self):
        testTable = self._database.table_with_implicit_array

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
        StructWithImplicit = self.api.StructWithImplicit
        return (i, StructWithImplicit([1, 2, 3, 4, 5]), "test" + str(i))

    NUM_ROWS = 5
