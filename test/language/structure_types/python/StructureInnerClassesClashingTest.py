import zserio

import StructureTypes

class StructureInnerClassesClashingTest(StructureTypes.TestCase):
    def testWriteReadArrayType(self):
        testStructure = self.api.ArrayType_array([1, 2, 3, 4])

        writer = zserio.BitStreamWriter()
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readTestStructure = self.api.ArrayType_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)

    def testWriteReadOffsetChecker(self):
        testStructure = self.api.OffsetChecker_array([0] * 4, [1, 2, 3, 4])

        writer = zserio.BitStreamWriter()
        testStructure.initialize_offsets(writer.bitposition)
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readTestStructure = self.api.OffsetChecker_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)

    def testWriteReadOffsetInitializer(self):
        testStructure = self.api.OffsetInitializer_array([0] * 4, [1, 2, 3, 4])

        writer = zserio.BitStreamWriter()
        testStructure.initialize_offsets(writer.bitposition)
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readTestStructure = self.api.OffsetInitializer_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)
