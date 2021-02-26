import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeAsChoiceFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_choice_field

    def testReadWrite(self):
        instantiateTypeAsChoiceField = self.api.InstantiateTypeAsChoiceField(True)
        instantiateTypeAsChoiceField.test = self.api.Test32(13)

        writer = zserio.BitStreamWriter()
        instantiateTypeAsChoiceField.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readInstantiateTypeAsChoiceField = self.api.InstantiateTypeAsChoiceField(True)
        readInstantiateTypeAsChoiceField.read(reader)
        self.assertEqual(instantiateTypeAsChoiceField, readInstantiateTypeAsChoiceField)
