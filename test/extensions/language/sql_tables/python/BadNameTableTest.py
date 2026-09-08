import os

import SqlTables


class BlobFieldWithChildrenInitializationTableTest(SqlTables.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.TestDb.from_file(self.dbFileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testTableCreated(self):
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='badNameTable'"
        if not self._database.connection.cursor().execute(sqlQuery):
            self.fail()

    def testTableOperations(self):
        testTable = self._database.select

        writtenRows = [(1, 35, 11, 5)]
        testTable.write(writtenRows)

        tmp = list(writtenRows[0])
        tmp[3] = 99
        row = tuple(tmp)
        testTable.update(row, "id=" + str(row[0]))

        readRows = testTable.read()
        self.assertTrue(readRows)
        for readRow in readRows:
            self.assertEqual(readRow, row)
