import unittest
import zserio

from testutils import getZserioApi

class FunctionTemplatedReturnTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").function_templated_return_type

    def testReadWrite(self):
        hasHolder = True
        functionTemplatedReturnType = self.api.FunctionTemplatedReturnType.fromFields(
            hasHolder,
            self.api.TestStructure_uint32.fromFields(
                hasHolder, None, self.api.Holder_uint32.fromFields(42)
            ),
            self.api.TestStructure_string.fromFields(
                hasHolder, None, self.api.Holder_string.fromFields("string")
            ),
            self.api.TestStructure_float32.fromFields(
                False, 4.2, None
            )
        )

        writer = zserio.BitStreamWriter()
        functionTemplatedReturnType.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readFunctionTemplatedReturnType = self.api.FunctionTemplatedReturnType()
        readFunctionTemplatedReturnType.read(reader)

        # float compare is a problem here, so hack the correct value
        self.assertAlmostEqual(4.2, readFunctionTemplatedReturnType.getFloatTest().getValue(), delta=0.001)
        readFunctionTemplatedReturnType.getFloatTest().setValue(4.2)

        self.assertEqual(functionTemplatedReturnType, readFunctionTemplatedReturnType)
