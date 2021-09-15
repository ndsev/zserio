import unittest
import zserio

from testutils import getZserioApi

# this test is mainly for C++, so just check that it is ok
class FieldConstructorClashingTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").field_constructor_clashing

    def testWriteRead(self):
        fieldConstructorClashing = self._createFieldConstructorClashing()

        writer = zserio.BitStreamWriter()
        fieldConstructorClashing.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFieldConstructorClashing = self.api.FieldConstructorClashing.from_reader(reader)
        self.assertEqual(fieldConstructorClashing, readFieldConstructorClashing)

    def _createFieldConstructorClashing(self):
        return self.api.FieldConstructorClashing(
            [self.api.CompoundRead(self.api.Field(self.FIELD1), self.api.Field(self.FIELD2))],
            [
                self.api.CompoundPackingRead(
                    self.api.Field(self.FIELD1), self.api.Field(self.FIELD2), self.api.Field(self.FIELD3)
                )
            ]
        )

    FIELD1 = 1
    FIELD2 = 9
    FIELD3 = 5
