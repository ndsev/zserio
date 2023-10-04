import unittest
import os
import apsw

from testutils import getZserioApi, getApiDir

class TableConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_constraints.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "table_constraints_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.from_file(self._fileName)
        self._database.create_schema()
        self._constraintsTable = self._database.table_constraints_table

    def tearDown(self):
        self._database.close()

    def testPrimaryKey(self):
        row = (1, 1, None, None)
        self._constraintsTable.write([row])

    def testPrimaryKeyWrong(self):
        row1 = (1, 1, 1, 1)
        row2 = (1, 1, 2, 1)
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([row1, row2])
        self.assertTrue(
            "UNIQUE constraint failed: tableConstraintsTable.primaryKey1, "
            "tableConstraintsTable.primaryKey2" in str(context.exception),
            str(context.exception))

    def testUnique(self):
        row = (1, 1, 1, 1)
        self._constraintsTable.write([row])

    def testUniqueWrong(self):
        row1 = (1, 1, 1, 1)
        row2 = (2, 1, 1, 1)
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([row1, row2])
        self.assertTrue(
            "UNIQUE constraint failed: tableConstraintsTable.uniqueValue1, "
            "tableConstraintsTable.uniqueValue2" in str(context.exception),
            str(context.exception))
