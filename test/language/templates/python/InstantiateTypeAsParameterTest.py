import zserio

import Templates


class InstantiateTypeAsParameterTest(Templates.TestCase):
    def testReadWrite(self):
        param = self.api.P32(2)
        instantiateTypeAsParameter = self.api.InstantiateTypeAsParameter(
            param, self.api.Parameterized_P32(param, [13, 42])
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeAsParameter.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsParameter = self.api.InstantiateTypeAsParameter()
        readInstantiateTypeAsParameter.read(reader)
        self.assertEqual(instantiateTypeAsParameter, readInstantiateTypeAsParameter)
