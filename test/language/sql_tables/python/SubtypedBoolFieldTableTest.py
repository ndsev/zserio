import unittest
import os

from testutils import getZserioApi, getApiDir

class SubtypedBoolFieldTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_tables.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                     "subtyped_bool_field_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.from_file(self._fileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testReadWithoutCondition(self):
        testTable = self._database.subtyped_bool_field_table

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

    @staticmethod
    def _createRow(i):
        return (i, i % 2 == 0)

    NUM_ROWS = 5
