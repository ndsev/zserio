import unittest

from testutils import getZserioApi

class OptionalArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_array

    def testData8(self):
        numElements = 4
        test = self.api.TestStruct()
        test.setHasData8(True)
        test.setData8([self.api.Data8.fromFields(i) for i in range(numElements)])
        self.assertEqual(numElements, len(test.getData8()))

    def testAutoData8(self):
        numElements = 5
        test = self.api.TestStruct()
        self.assertFalse(test.hasAutoData8())
        test.setAutoData8([self.api.Data8.fromFields(i) for i in range(numElements)])
        self.assertTrue(test.hasAutoData8())
        self.assertEqual(numElements, len(test.getAutoData8()))

    def testData16(self):
        numElements = 6
        test = self.api.TestStruct()
        test.setHasData8(False)
        test.setData16(list(range(numElements)))
        self.assertEqual(numElements, len(test.getData16()))

    def testAutoData16(self):
        numElements = 7
        test = self.api.TestStruct()
        self.assertFalse(test.hasAutoData16())
        test.setAutoData16(list(range(numElements)))
        self.assertTrue(test.hasAutoData16())
        self.assertEqual(numElements, len(test.getAutoData16()))
