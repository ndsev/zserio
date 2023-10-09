import os

import Templates

class SqlTableTemplatedFieldTest(Templates.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)

    def testReadWrite(self):
        sqlTableTemplatedFieldDb = self.api.SqlTableTemplatedFieldDb.from_file(self.dbFileName)
        sqlTableTemplatedFieldDb.create_schema()

        uint32Table = sqlTableTemplatedFieldDb.uint32_table
        uint32TableRows = [(0, self.api.Data_uint32(42))]
        uint32Table.write(uint32TableRows)

        unionTable = sqlTableTemplatedFieldDb.union_table
        union1 = self.api.Union(value_string_="string")
        unionTableRows = [(13, self.api.Data_Union(union1))]
        unionTable.write(unionTableRows)

        sqlTableTemplatedFieldDb.close()

        readSqlTableTemplatedFieldDb = self.api.SqlTableTemplatedFieldDb.from_file(self.dbFileName)
        readUint32TableIterator = readSqlTableTemplatedFieldDb.uint32_table.read()
        readUint32TableRows = []
        for row in readUint32TableIterator:
            readUint32TableRows.append(row)
        readUnionTableIterator = readSqlTableTemplatedFieldDb.union_table.read()
        readUnionTableRows = []
        for row in readUnionTableIterator:
            readUnionTableRows.append(row)

        self.assertEqual(uint32TableRows, readUint32TableRows)
        self.assertEqual(unionTableRows, readUnionTableRows)
