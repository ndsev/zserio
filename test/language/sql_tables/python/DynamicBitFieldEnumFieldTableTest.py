import unittest
import os

from testutils import getZserioApi, getApiDir

class DynamicBitFieldEnumFieldTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_tables.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                     "dynamic_bit_field_enum_field_table_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.from_file(self._fileName)
        self._database.create_schema()

    def tearDown(self):
        self._database.close()

    def testReadWithoutCondition(self):
        testTable = self._database.dynamic_bit_field_enum_field_table

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
        TestEnum = self.api.dynamic_bit_field_enum_field_table.TestEnum
        return (i, TestEnum.ONE if i % 3 == 0 else (TestEnum.TWO if i % 3 == 1 else TestEnum.THREE))

    NUM_ROWS = 5
