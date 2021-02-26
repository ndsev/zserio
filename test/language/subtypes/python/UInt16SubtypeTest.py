import unittest

from testutils import getZserioApi

class UInt16SubtypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").uint16_subtype

    def testSubtype(self):
        identifier = self.api.Identifier(0xFFFF)
        testStructure = self.api.TestStructure()
        testStructure.identifier = identifier
        readIdentifier = testStructure.identifier
        self.assertEqual(identifier, readIdentifier)
