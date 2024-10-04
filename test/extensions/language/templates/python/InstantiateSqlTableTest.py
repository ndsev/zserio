import apsw

import Templates


class InstantiateSqlTableTest(Templates.TestCase):
    def testReadWrite(self):
        connection = apsw.Connection(
            self.SQLITE3_MEM_DB, apsw.SQLITE_OPEN_URI | apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE
        )

        u32Table = self.api.U32Table(connection, "u32Table")
        u32Table.create_table()

        rows = [(13, "info")]
        u32Table.write(rows)

        readIterator = u32Table.read()
        readRows = []
        for row in readIterator:
            readRows.append(row)

        self.assertEqual(rows, readRows)

    SQLITE3_MEM_DB = ":memory:"
