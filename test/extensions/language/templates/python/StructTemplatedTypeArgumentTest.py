import zserio

import Templates


class StructTemplatedTypeArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        paramHolder = self.api.ParamHolder_uint32(42)
        structTemplatedTypeArgument = self.api.StructTemplatedTypeArgument(
            paramHolder, self.api.Parameterized_uint32(paramHolder, "description", 13)
        )

        writer = zserio.BitStreamWriter()
        structTemplatedTypeArgument.initialize_offsets(writer.bitposition)
        structTemplatedTypeArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructTemplatedTypeArgument = self.api.StructTemplatedTypeArgument()
        readStructTemplatedTypeArgument.read(reader)
        self.assertEqual(structTemplatedTypeArgument, readStructTemplatedTypeArgument)
