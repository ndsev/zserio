import zserio

import StructureTypes

class StructureWithRemovedEnumItemFieldTest(StructureTypes.TestCase):
    def testConstructor(self):
        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField(
            self.api.Enumeration.ZSERIO_REMOVED_REMOVED
        )
        self.assertEqual(self.api.Enumeration.ZSERIO_REMOVED_REMOVED,
                         structureWithRemovedEnumItemField.enumeration)

    def testFromReader(self):
        reader = zserio.BitStreamReader(bytearray([0x00]))

        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField.from_reader(reader)
        self.assertEqual(self.api.Enumeration.ZSERIO_REMOVED_REMOVED,
                         structureWithRemovedEnumItemField.enumeration)

    def testSetter(self):
        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField()
        structureWithRemovedEnumItemField.enumeration = self.api.Enumeration.ZSERIO_REMOVED_REMOVED
        self.assertEqual(self.api.Enumeration.ZSERIO_REMOVED_REMOVED,
                         structureWithRemovedEnumItemField.enumeration)

    def testWriteValid(self):
        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField(
            self.api.Enumeration.VALID
        )
        try:
            zserio.serialize(structureWithRemovedEnumItemField)
        except zserio.PythonRuntimeException:
            self.fail("PythonRuntimeException raised unexpectedly!")

    def testWriteRemovedException(self):
        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField(
            self.api.Enumeration.ZSERIO_REMOVED_REMOVED
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(structureWithRemovedEnumItemField)

    def testToJsonString(self):
        structureWithRemovedEnumItemField = self.api.StructureWithRemovedEnumItemField(
            self.api.Enumeration.ZSERIO_REMOVED_REMOVED
        )
        json = zserio.to_json_string(structureWithRemovedEnumItemField)
        self.assertEqual("{\n    \"enumeration\": \"REMOVED\"\n}", json)

    def testFromJsonString(self):
        structureWithRemovedEnumItemField = zserio.from_json_string(
            self.api.StructureWithRemovedEnumItemField, "{\n    \"enumeration\": \"REMOVED\"\n}"
        )
        self.assertEqual(self.api.Enumeration.ZSERIO_REMOVED_REMOVED,
                         structureWithRemovedEnumItemField.enumeration)
