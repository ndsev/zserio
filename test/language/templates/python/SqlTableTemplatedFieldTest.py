import unittest
import os

from testutils import getZserioApi, getApiDir

class SqlTableTemplatedFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").sql_table_templated_field
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "sql_table_templated_field.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)

    def testReadWrite(self):
        sqlTableTemplatedFieldDb = self.api.SqlTableTemplatedFieldDb.from_file(self._fileName)
        sqlTableTemplatedFieldDb.create_schema()

        uint32Table = sqlTableTemplatedFieldDb.uint32table
        uint32TableRows = [(0, self.api.Data_uint32(42))]
        uint32Table.write(uint32TableRows)

        unionTable = sqlTableTemplatedFieldDb.union_table
        union1 = self.api.Union(value_string_="string")
        unionTableRows = [(13, self.api.Data_Union(union1))]
        unionTable.write(unionTableRows)

        sqlTableTemplatedFieldDb.close()

        readSqlTableTemplatedFieldDb = self.api.SqlTableTemplatedFieldDb.from_file(self._fileName)
        readUint32TableIterator = readSqlTableTemplatedFieldDb.uint32table.read()
        readUint32TableRows = []
        for row in readUint32TableIterator:
            readUint32TableRows.append(row)
        readUnionTableIterator = readSqlTableTemplatedFieldDb.union_table.read()
        readUnionTableRows = []
        for row in readUnionTableIterator:
            readUnionTableRows.append(row)

        self.assertEqual(uint32TableRows, readUint32TableRows)
        self.assertEqual(unionTableRows, readUnionTableRows)
