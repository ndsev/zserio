import unittest
import apsw

from testutils import getZserioApi

class InstantiateTypeAsSqlTableFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_sql_table_field

    def testReadWrite(self):
        connection = apsw.Connection(
            self.SQLITE3_MEM_DB,
            apsw.SQLITE_OPEN_URI | apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE
        )

        test32Table = self.api.Test32Table(connection, "test32Table")
        test32Table.createTable()

        rows = [(13, self.api.Test32.fromFields(42))]
        test32Table.write(rows)

        readIterator = test32Table.read()
        readRows = []
        for row in readIterator:
            readRows.append(row)

        self.assertEqual(rows, readRows)


    SQLITE3_MEM_DB = ":memory:"
