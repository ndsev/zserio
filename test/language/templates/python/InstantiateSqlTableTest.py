import unittest
import apsw

from testutils import getZserioApi

class InstantiateSqlTableTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_sql_table

    def testReadWrite(self):
        connection = apsw.Connection(
            self.SQLITE3_MEM_DB,
            apsw.SQLITE_OPEN_URI | apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE
        )

        u32Table = self.api.U32Table(connection, "u32Table")
        u32Table.createTable()

        rows = [(13, "info")]
        u32Table.write(rows)

        readIterator = u32Table.read()
        readRows = []
        for row in readIterator:
            readRows.append(row)

        self.assertEqual(rows, readRows)


    SQLITE3_MEM_DB = ":memory:"
