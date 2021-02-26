import unittest

from testutils import getZserioApi

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
