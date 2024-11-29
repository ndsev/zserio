import zserio

import Expressions


class LengthOfOperatorTest(Expressions.TestCase):
    def testGetLengthOfFixedArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        fixedArrayLength = 10
        lengthOfFunctions.fixed_array = list(range(fixedArrayLength))
        self.assertEqual(fixedArrayLength, lengthOfFunctions.get_length_of_fixed_array())

    def testGetLengthOfVariableArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        variableArrayLength = 11
        lengthOfFunctions.num_elements = variableArrayLength
        lengthOfFunctions.variable_array = list(range(variableArrayLength))
        self.assertEqual(variableArrayLength, lengthOfFunctions.get_length_of_variable_array())

    def testGetLengthOfStrContant(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        # check that it's length in bytes (UTF-8)
        self.assertEqual(11, zserio.builtin.lengthof_string("€constant"))
        self.assertEqual(
            zserio.builtin.lengthof_string(self.api.STR_CONSTANT),
            lengthOfFunctions.get_length_of_str_constant(),
        )

    def testGetLengthOfLiteral(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        # check that it's length in bytes (UTF-8)
        self.assertEqual(10, zserio.builtin.lengthof_string("€literal"))
        self.assertEqual(10, lengthOfFunctions.get_length_of_literal())

    def testLiteralLengthFieldDefault(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        # check that it's length in bytes (UTF-8)
        self.assertEqual(10, zserio.builtin.lengthof_string("€literal"))
        self.assertEqual(10, lengthOfFunctions.literal_length_field)

    def testGetLengthOfString(self):
        strField = "€test"
        lengthOfFunctions = self.api.LengthOfFunctions()
        lengthOfFunctions.str_field = strField
        # check that it's length in bytes (UTF-8)
        self.assertEqual(7, zserio.builtin.lengthof_string(strField))
        self.assertEqual(zserio.builtin.lengthof_string(strField), lengthOfFunctions.get_length_of_string())

    def testGetLengthOfBytes(self):
        bytesField = bytearray([0x00, 0x01, 0x02])
        lengthOfFunctions = self.api.LengthOfFunctions()
        lengthOfFunctions.bytes_field = bytesField
        self.assertEqual(len(bytesField), lengthOfFunctions.get_length_of_bytes())

    def testGetLengthOfFirstStrInArray(self):
        strArray = ["€", "$"]
        lengthOfFunctions = self.api.LengthOfFunctions()
        lengthOfFunctions.str_array = strArray
        # check that it's length in bytes (UTF-8)
        self.assertEqual(3, zserio.builtin.lengthof_string(strArray[0]))
        self.assertEqual(
            zserio.builtin.lengthof_string(strArray[0]),
            lengthOfFunctions.get_length_of_first_str_in_array(),
        )

    def testGetLengthOfFirstBytesInArray(self):
        bytesArray = [bytearray([0x00, 0x01]), bytearray([])]
        lengthOfFunctions = self.api.LengthOfFunctions()
        lengthOfFunctions.bytes_array = bytesArray
        self.assertEqual(len(bytesArray[0]), lengthOfFunctions.get_length_of_first_bytes_in_array())

    def testWriteRead(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        lengthOfFunctions.fixed_array = [0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09]
        lengthOfFunctions.num_elements = 3
        lengthOfFunctions.variable_array = [0x03, 0x02, 0x01]
        lengthOfFunctions.str_field = "longer than constant"
        lengthOfFunctions.bytes_field = bytearray([0x00, 0x01, 0x02])
        lengthOfFunctions.str_array = []
        lengthOfFunctions.bytes_array = []

        bitBuffer = zserio.serialize(lengthOfFunctions)
        readLengthOfFunctions = zserio.deserialize(self.api.LengthOfFunctions, bitBuffer)
        self.assertEqual(lengthOfFunctions, readLengthOfFunctions)
