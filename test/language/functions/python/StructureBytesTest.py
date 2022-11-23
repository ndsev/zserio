import unittest

from testutils import getZserioApi

class StructureBytesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_bytes

    def testGetField(self):
        testStructure = self.api.TestStructure(self.FIELD, self.api.Child(self.CHILD_FIELD))
        self.assertEqual(self.FIELD, testStructure.get_field())

    def testGetChildField(self):
        testStructure = self.api.TestStructure(self.FIELD, self.api.Child(self.CHILD_FIELD))
        self.assertEqual(self.CHILD_FIELD, testStructure.get_child_field())

    FIELD = bytes([0xAB, 0xE0])
    CHILD_FIELD = bytes([0xCA, 0xFE])
