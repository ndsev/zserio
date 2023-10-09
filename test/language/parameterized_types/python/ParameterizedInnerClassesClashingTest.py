import zserio

import ParameterizedTypes

class ParameterizedInnerClassesClashingTest(ParameterizedTypes.TestCase):
    def testWriteReadElementFactory(self):
        param = 100
        testStructure = self.api.ElementFactory_array(param,
            [self.api.Compound(param, 13), self.api.Compound(param, 42)]
        )

        writer = zserio.BitStreamWriter()
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        readTestStructure = self.api.ElementFactory_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)

    def testWriteReadElementInitializer(self):
        param = 100
        testStructure = self.api.ElementInitializer_array(param,
            [self.api.Compound(param, 13), self.api.Compound(param, 42)]
        )

        writer = zserio.BitStreamWriter()
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        readTestStructure = self.api.ElementInitializer_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)

    def testWriteReadElementChildrenInitializer(self):
        param = 100
        testStructure = self.api.ElementChildrenInitializer_array([
            self.api.Parent(param, self.api.Compound(param, 13)),
            self.api.Parent(param, self.api.Compound(param, 42))
        ])

        writer = zserio.BitStreamWriter()
        testStructure.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        readTestStructure = self.api.ElementChildrenInitializer_array.from_reader(reader)
        self.assertEqual(testStructure, readTestStructure)
