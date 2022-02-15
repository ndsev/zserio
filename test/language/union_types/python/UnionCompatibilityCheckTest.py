import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class UnionCompatibilityCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_compatibility_check

    def testWriteVersion1ReadVersion1(self):
        unionCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readUnionCompatibilityCheckVersion1 = self._writeReadVersion1(unionCompatibilityCheckVersion1)

        self.assertEqual(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1)

    def testWriteVersion1ReadVersion2(self):
        unionCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readUnionCompatibilityCheckVersion2 = self._writeReadVersion2(unionCompatibilityCheckVersion1)

        expectedArrayVersion2 = self._createArrayVersion2WithVersion1Fields()
        self.assertEqual(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.array)
        self.assertEqual(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.packed_array)

    def testWriteVersion2ReadVersion1(self):
        unionCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2WithVersion1Fields())
        readUnionCompatibilityCheckVersion1 = self._writeReadVersion1(unionCompatibilityCheckVersion2)

        expectedArrayVersion1 = self._createArrayVersion1()
        self.assertEqual(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.array)
        self.assertEqual(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.packed_array)

    def testWriteVersion2ReadVersion2(self):
        unionCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2())
        readUnionCompatibilityCheckVersion2 = self._writeReadVersion2(unionCompatibilityCheckVersion2)

        self.assertEqual(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2)

    def testWriteVersion1ReadVersion1File(self):
        unionCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readUnionCompatibilityCheckVersion1 = self._writeReadVersion1File(unionCompatibilityCheckVersion1,
                                                                           "version1_version1")

        self.assertEqual(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1)

    def testWriteVersion1ReadVersion2File(self):
        unionCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readUnionCompatibilityCheckVersion2 = self._writeReadVersion2File(unionCompatibilityCheckVersion1,
                                                                           "version1_version2")

        expectedArrayVersion2 = self._createArrayVersion2WithVersion1Fields()
        self.assertEqual(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.array)
        self.assertEqual(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.packed_array)

    def testWriteVersion2ReadVersion1File(self):
        unionCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2WithVersion1Fields())
        readUnionCompatibilityCheckVersion1 = self._writeReadVersion1File(unionCompatibilityCheckVersion2,
                                                                           "version2_version1")

        expectedArrayVersion1 = self._createArrayVersion1()
        self.assertEqual(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.array)
        self.assertEqual(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.packed_array)

    def testWriteVersion2ReadVersion2File(self):
        unionCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2())
        readUnionCompatibilityCheckVersion2 = self._writeReadVersion2File(unionCompatibilityCheckVersion2,
                                                                           "version2_version2")

        self.assertEqual(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2)

    def _createVersion1(self, array):
        return self.api.UnionCompatibilityCheckVersion1(array, array)

    def _createVersion2(self, array):
        return self.api.UnionCompatibilityCheckVersion2(array, array)

    def _createArrayVersion1(self):
        return [
            self._createUnionVersion1(0),
            self._createUnionVersion1(1),
            self._createUnionVersion1(2),
            self._createUnionVersion1(3)
        ]

    def _createArrayVersion2WithVersion1Fields(self):
        return [
            self._createUnionVersion2(0),
            self._createUnionVersion2(1),
            self._createUnionVersion2(2),
            self._createUnionVersion2(3)
        ]

    def _createArrayVersion2(self):
        return self._createArrayVersion2WithVersion1Fields() + [
            self._createUnionCoordXYZ(4),
            self._createUnionCoordXYZ(5),
            self._createUnionCoordXYZ(6)
        ]

    def _createUnionVersion1(self, index):
        union = self.api.UnionVersion1()
        if index % 2 == 0:
            union.coord_xy = self.api.CoordXY(10 * index, 20 * index)
        else:
            union.text = "text" + str(index)
        return union

    def _createUnionVersion2(self, index):
        union = self.api.UnionVersion2()
        if index % 2 == 0:
            union.coord_xy = self.api.CoordXY(10 * index, 20 * index)
        else:
            union.text = "text" + str(index)
        return union

    def _createUnionCoordXYZ(self, index):
        union = self.api.UnionVersion2()
        union.coord_xyz = self.api.CoordXYZ(10 * index, 20 * index, 1.1 * index)
        return union

    def _writeReadVersion1(self, unionCompatibilityCheck):
        writer = zserio.BitStreamWriter()
        unionCompatibilityCheck.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        return self.api.UnionCompatibilityCheckVersion1.from_reader(reader)

    def _writeReadVersion2(self, unionCompatibilityCheck):
        writer = zserio.BitStreamWriter()
        unionCompatibilityCheck.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        return self.api.UnionCompatibilityCheckVersion2.from_reader(reader)

    def _writeReadVersion1File(self, unionCompatibilityCheck, variant):
        filename = self.BLOB_NAME_BASE + variant + ".blob"
        zserio.serialize_to_file(unionCompatibilityCheck, filename)
        return zserio.deserialize_from_file(self.api.UnionCompatibilityCheckVersion1, filename)

    def _writeReadVersion2File(self, unionCompatibilityCheck, variant):
        filename = self.BLOB_NAME_BASE + variant + ".blob"
        zserio.serialize_to_file(unionCompatibilityCheck, filename)
        return zserio.deserialize_from_file(self.api.UnionCompatibilityCheckVersion2, filename)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "union_compatibility_check_")
