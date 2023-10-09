import apsw

import Templates

class InstantiateTypeAsSqlTableFieldTest(Templates.TestCase):
    def testReadWrite(self):
        connection = apsw.Connection(
            self.SQLITE3_MEM_DB,
            apsw.SQLITE_OPEN_URI | apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE
        )

        test32Table = self.api.Test32Table(connection, "test32Table")
        test32Table.create_table()

        rows = [(13, self.api.Test32(42))]
        test32Table.write(rows)

        readIterator = test32Table.read()
        readRows = []
        for row in readIterator:
            readRows.append(row)

        self.assertEqual(rows, readRows)


    SQLITE3_MEM_DB = ":memory:"
