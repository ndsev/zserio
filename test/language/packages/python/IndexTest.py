import unittest
import zserio

from testutils import getZserioApi

class IndexTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "index.zs")

    def testReadWrite(self):
        test = self.api.Test()
        test.indexes = [ 0 ] * self.ARRAY_SIZE
        test.indexes_for_parameterized = [ 0 ] * self.ARRAY_SIZE
        test.array = [ self.api.Element(i) for i in range(self.ARRAY_SIZE) ]
        test.parameterized_array = [ self.api.ParameterizedElement(i, i) for i in range(self.ARRAY_SIZE) ]

        writer = zserio.BitStreamWriter()
        test.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array)
        readTest = self.api.Test.from_reader(reader)

        self.assertEqual(self.ARRAY_SIZE, len(readTest.array))
        self.assertEqual(self.ARRAY_SIZE, len(readTest.parameterized_array))
        self.assertEqual(test, readTest)

    ARRAY_SIZE = 10
