import os
import zserio

import UnionTypes
from testutils import getApiDir

class UnionWithArrayTest(UnionTypes.TestCase):
    def testArray8(self):
        testUnion = self.api.TestUnion()
        testUnion.array8 = [self.api.Data8(), self.api.Data8(), self.api.Data8(), self.api.Data8()]
        self.assertEqual(4, len(testUnion.array8))

    def testArray16(self):
        testUnion = self.api.TestUnion(array16_=[1, 2, 3, 4])
        self.assertEqual(4, len(testUnion.array16))

    def testWriteReadFileArray8(self):
        testUnion = self.api.TestUnion(array8_=self._createArray8())
        filename = self.BLOB_NAME_BASE + "array8.blob"
        zserio.serialize_to_file(testUnion, filename)

        readTestUnion = zserio.deserialize_from_file(self.api.TestUnion, filename)
        self.assertEqual(testUnion, readTestUnion)

    def testWriteReadFileArray16(self):
        testUnion = self.api.TestUnion(array16_=self._createArray16())
        filename = self.BLOB_NAME_BASE + "array16.blob"
        zserio.serialize_to_file(testUnion, filename)

        readTestUnion = zserio.deserialize_from_file(self.api.TestUnion, filename)
        self.assertEqual(testUnion, readTestUnion)

    def testHash(self):
        testUnion1 = self.api.TestUnion()
        testUnion2 = self.api.TestUnion()
        self.assertEqual(hash(testUnion1), hash(testUnion2))
        testUnion1.array8 = self._createArray8()
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.array8 = self._createArray8()
        self.assertEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.array8[0].data = 0
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.array16 = self._createArray16()
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(87386744, testUnion1.__hash__())
        self.assertEqual(1575145265, testUnion2.__hash__())

        testUnion1.array16 = self._createArray16()
        self.assertEqual(hash(testUnion1), hash(testUnion2))

    def _createArray8(self):
        return [self.api.Data8(-1), self.api.Data8(-2), self.api.Data8(-3), self.api.Data8(-4)]

    @staticmethod
    def _createArray16():
        return [-10, -20, -30, -40, -50]

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "union_with_array_")
