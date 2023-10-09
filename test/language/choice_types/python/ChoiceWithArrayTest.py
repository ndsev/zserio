import os
import zserio

import ChoiceTypes

from testutils import getApiDir

class ChoiceWithArrayTest(ChoiceTypes.TestCase):
    def testArray8(self):
        testChoice = self.api.TestChoice(8)
        testChoice.array8 = [self.api.Data8(), self.api.Data8(), self.api.Data8(), self.api.Data8()]
        self.assertEqual(4, len(testChoice.array8))

    def testArray16(self):
        testChoice = self.api.TestChoice(16)
        testChoice.array16 = [1, 2, 3, 4]
        self.assertEqual(4, len(testChoice.array16))

    def testWriteReadFileArray8(self):
        testChoice = self.api.TestChoice(8)
        testChoice.array8 = [self.api.Data8(1), self.api.Data8(2), self.api.Data8(3), self.api.Data8(4)]
        filename = self.BLOB_NAME_BASE + "array8.blob"
        zserio.serialize_to_file(testChoice, filename)

        readTestChoice = zserio.deserialize_from_file(self.api.TestChoice, filename, 8)
        self.assertEqual(testChoice, readTestChoice)

    def testWriteReadFileArray16(self):
        testChoice = self.api.TestChoice(16)
        testChoice.array16 = [10, 20, 30, 40, 50]
        filename = self.BLOB_NAME_BASE + "array16.blob"
        zserio.serialize_to_file(testChoice, filename)

        readTestChoice = zserio.deserialize_from_file(self.api.TestChoice, filename, 16)
        self.assertEqual(testChoice, readTestChoice)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "choice_with_array_")
