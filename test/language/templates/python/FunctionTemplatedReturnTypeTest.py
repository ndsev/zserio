import zserio

import Templates


class FunctionTemplatedReturnTypeTest(Templates.TestCase):
    def testReadWrite(self):
        hasHolder = True
        functionTemplatedReturnType = self.api.FunctionTemplatedReturnType(
            hasHolder,
            self.api.TestStructure_uint32(hasHolder, holder_=self.api.Holder_uint32(42)),
            self.api.TestStructure_string(hasHolder, holder_=self.api.Holder_string("string")),
            self.api.TestStructure_float32(False, value_=4.2),
        )

        writer = zserio.BitStreamWriter()
        functionTemplatedReturnType.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFunctionTemplatedReturnType = self.api.FunctionTemplatedReturnType()
        readFunctionTemplatedReturnType.read(reader)

        # float compare is a problem here, so hack the correct value
        self.assertAlmostEqual(4.2, readFunctionTemplatedReturnType.float_test.value, delta=0.001)
        readFunctionTemplatedReturnType.float_test.value = 4.2

        self.assertEqual(functionTemplatedReturnType, readFunctionTemplatedReturnType)
