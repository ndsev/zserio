import unittest
import zserio

from testutils import getZserioApi

class FunctionTemplatedReturnTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").function_templated_return_type

    def testReadWrite(self):
        hasHolder = True
        functionTemplatedReturnType = self.api.FunctionTemplatedReturnType(
            hasHolder,
            self.api.TestStructure_uint32(hasHolder, holder_=self.api.Holder_uint32(42)),
            self.api.TestStructure_string(hasHolder, holder_=self.api.Holder_string("string")),
            self.api.TestStructure_float32(False, value_=4.2)
        )

        writer = zserio.BitStreamWriter()
        functionTemplatedReturnType.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFunctionTemplatedReturnType = self.api.FunctionTemplatedReturnType()
        readFunctionTemplatedReturnType.read(reader)

        # float compare is a problem here, so hack the correct value
        self.assertAlmostEqual(4.2, readFunctionTemplatedReturnType.getFloatTest().getValue(), delta=0.001)
        readFunctionTemplatedReturnType.getFloatTest().setValue(4.2)

        self.assertEqual(functionTemplatedReturnType, readFunctionTemplatedReturnType)
