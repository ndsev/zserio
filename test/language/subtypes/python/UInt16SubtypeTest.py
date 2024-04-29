import Subtypes


class UInt16SubtypeTest(Subtypes.TestCase):
    def testSubtype(self):
        identifier = self.api.Identifier(0xFFFF)
        testStructure = self.api.TestStructure()
        testStructure.identifier = identifier
        readIdentifier = testStructure.identifier
        self.assertEqual(identifier, readIdentifier)
