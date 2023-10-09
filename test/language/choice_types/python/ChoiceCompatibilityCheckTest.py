import os
import zserio

import ChoiceTypes

from testutils import getApiDir

class ChoiceCompatibilityCheckTest(ChoiceTypes.TestCase):
    def testWriteVersion1ReadVersion1(self):
        choiceCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readChoiceCompatibilityCheckVersion1 = self._writeReadVersion1(choiceCompatibilityCheckVersion1)

        self.assertEqual(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1)

    def testWriteVersion1ReadVersion2(self):
        choiceCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readChoiceCompatibilityCheckVersion2 = self._writeReadVersion2(choiceCompatibilityCheckVersion1)

        expectedArrayVersion2 = self._createArrayVersion2WithVersion1Fields()
        self.assertEqual(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.array)
        self.assertEqual(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.packed_array)

    def testWriteVersion2ReadVersion1(self):
        choiceCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2WithVersion1Fields())
        readChoiceCompatibilityCheckVersion1 = self._writeReadVersion1(choiceCompatibilityCheckVersion2)

        expectedArrayVersion1 = self._createArrayVersion1()
        self.assertEqual(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.array)
        self.assertEqual(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.packed_array)

    def testWriteVersion2ReadVersion2(self):
        choiceCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2())
        readChoiceCompatibilityCheckVersion2 = self._writeReadVersion2(choiceCompatibilityCheckVersion2)

        self.assertEqual(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2)

    def testWriteVersion1ReadVersion1File(self):
        choiceCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readChoiceCompatibilityCheckVersion1 = self._writeReadVersion1File(choiceCompatibilityCheckVersion1,
                                                                           "version1_version1")

        self.assertEqual(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1)

    def testWriteVersion1ReadVersion2File(self):
        choiceCompatibilityCheckVersion1 = self._createVersion1(self._createArrayVersion1())
        readChoiceCompatibilityCheckVersion2 = self._writeReadVersion2File(choiceCompatibilityCheckVersion1,
                                                                           "version1_version2")

        expectedArrayVersion2 = self._createArrayVersion2WithVersion1Fields()
        self.assertEqual(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.array)
        self.assertEqual(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.packed_array)

    def testWriteVersion2ReadVersion1File(self):
        choiceCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2WithVersion1Fields())
        readChoiceCompatibilityCheckVersion1 = self._writeReadVersion1File(choiceCompatibilityCheckVersion2,
                                                                           "version2_version1")

        expectedArrayVersion1 = self._createArrayVersion1()
        self.assertEqual(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.array)
        self.assertEqual(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.packed_array)

    def testWriteVersion2ReadVersion2File(self):
        choiceCompatibilityCheckVersion2 = self._createVersion2(self._createArrayVersion2())
        readChoiceCompatibilityCheckVersion2 = self._writeReadVersion2File(choiceCompatibilityCheckVersion2,
                                                                           "version2_version2")

        self.assertEqual(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2)

    def _createVersion1(self, array):
        return self.api.ChoiceCompatibilityCheckVersion1(array, array)

    def _createVersion2(self, array):
        return self.api.ChoiceCompatibilityCheckVersion2(array, array)

    def _createArrayVersion1(self):
        return [
            self._createHolderVersion1(self.api.EnumVersion1.COORD_XY, 0),
            self._createHolderVersion1(self.api.EnumVersion1.TEXT, 1),
            self._createHolderVersion1(self.api.EnumVersion1.COORD_XY, 2),
            self._createHolderVersion1(self.api.EnumVersion1.TEXT, 3)
        ]

    def _createArrayVersion2WithVersion1Fields(self):
        return [
            self._createHolderVersion2(self.api.EnumVersion2.COORD_XY, 0),
            self._createHolderVersion2(self.api.EnumVersion2.TEXT, 1),
            self._createHolderVersion2(self.api.EnumVersion2.COORD_XY, 2),
            self._createHolderVersion2(self.api.EnumVersion2.TEXT, 3)
        ]

    def _createArrayVersion2(self):
        return self._createArrayVersion2WithVersion1Fields() + [
            self._createHolderVersion2(self.api.EnumVersion2.COORD_XYZ, 4),
            self._createHolderVersion2(self.api.EnumVersion2.COORD_XYZ, 5),
            self._createHolderVersion2(self.api.EnumVersion2.COORD_XYZ, 6)
        ]

    def _createHolderVersion1(self, selector, index):
        choice = self.api.ChoiceVersion1(selector)
        if selector == self.api.EnumVersion1.COORD_XY:
            choice.coord_xy = self.api.CoordXY(10 * index, 20 * index)
        else:
            choice.text = "text" + str(index)
        return self.api.HolderVersion1(selector, choice)

    def _createHolderVersion2(self, selector, index):
        choice = self.api.ChoiceVersion2(selector)
        if selector == self.api.EnumVersion2.COORD_XY:
            choice.coord_xy = self.api.CoordXY(10 * index, 20 * index)
        elif selector == self.api.EnumVersion2.TEXT:
            choice.text = "text" + str(index)
        else:
            choice.coord_xyz = self.api.CoordXYZ(10 * index, 20 * index, 1.1 * index)
        return self.api.HolderVersion2(selector, choice)

    def _writeReadVersion1(self, choiceCompatibilityCheck):
        writer = zserio.BitStreamWriter()
        choiceCompatibilityCheck.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        return self.api.ChoiceCompatibilityCheckVersion1.from_reader(reader)

    def _writeReadVersion2(self, choiceCompatibilityCheck):
        writer = zserio.BitStreamWriter()
        choiceCompatibilityCheck.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        return self.api.ChoiceCompatibilityCheckVersion2.from_reader(reader)

    def _writeReadVersion1File(self, choiceCompatibilityCheck, variant):
        filename = self.BLOB_NAME_BASE + variant + ".blob"
        zserio.serialize_to_file(choiceCompatibilityCheck, filename)
        return zserio.deserialize_from_file(self.api.ChoiceCompatibilityCheckVersion1, filename)

    def _writeReadVersion2File(self, choiceCompatibilityCheck, variant):
        filename = self.BLOB_NAME_BASE + variant + ".blob"
        zserio.serialize_to_file(choiceCompatibilityCheck, filename)
        return zserio.deserialize_from_file(self.api.ChoiceCompatibilityCheckVersion2, filename)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "choice_compatibility_check_")
