import unittest

from testutils import getZserioApi

class UnionWithArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_array

    def testArray8(self):
        testUnion = self.api.TestUnion()
        testUnion.setArray8([self.api.Data8(), self.api.Data8(), self.api.Data8(), self.api.Data8()])
        self.assertEqual(4, len(testUnion.getArray8()))

    def testArray16(self):
        testUnion = self.api.TestUnion()
        testUnion.setArray16([1, 2, 3, 4])
        self.assertEqual(4, len(testUnion.getArray16()))
