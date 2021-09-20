import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class UnionWithArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_array

    def testArray8(self):
        testUnion = self.api.TestUnion()
        testUnion.array8 = [self.api.Data8(), self.api.Data8(), self.api.Data8(), self.api.Data8()]
        self.assertEqual(4, len(testUnion.array8))

    def testArray16(self):
        testUnion = self.api.TestUnion(array16_=[1, 2, 3, 4])
        self.assertEqual(4, len(testUnion.array16))

    def testWriteReadFileArray8(self):
        testUnion = self.api.TestUnion(array8_=[
            self.api.Data8(-1), self.api.Data8(-2), self.api.Data8(-3), self.api.Data8(-4)
        ])
        filename = self.BLOB_NAME_BASE + "array8.blob"
        zserio.serialize_to_file(testUnion, filename)

        readTestUnion = zserio.deserialize_from_file(self.api.TestUnion, filename)
        self.assertEqual(testUnion, readTestUnion)

    def testWriteReadFileArray16(self):
        testUnion = self.api.TestUnion(array16_=[-10, -20, -30, -40, -50])
        filename = self.BLOB_NAME_BASE + "array16.blob"
        zserio.serialize_to_file(testUnion, filename)

        readTestUnion = zserio.deserialize_from_file(self.api.TestUnion, filename)
        self.assertEqual(testUnion, readTestUnion)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "union_with_array_")
