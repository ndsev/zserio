import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class FixedAndVariableParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").fixed_and_variable_param
        cls.COLOR = cls.api.Color.BLACK
        cls.WRONG_COLOR = cls.api.Color.WHITE
        cls.ACCESS = cls.api.Access.Values.READ
        cls.WRONG_ACCESS = cls.api.Access.Values.WRITE

    def testWrite(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        writer = zserio.BitStreamWriter()
        fixedAndVariableParam.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkFixedAndVariableParamInStream(reader, fixedAndVariableParam, self.ARRAY_SIZE,
                                                 self.EXTRA_LIMIT, self.LIMIT, self.COLOR, self.ACCESS,
                                                 self.FLOAT_VALUE)

        reader.bitposition = 0
        readFixedAndVariableParam = self.api.FixedAndVariableParam.from_reader(reader)
        self.assertEqual(fixedAndVariableParam, readFixedAndVariableParam)

    def testWriteFile(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        zserio.serialize_to_file(fixedAndVariableParam, self.BLOB_NAME)

        readFixedAndVariableParam = zserio.deserialize_from_file(self.api.FixedAndVariableParam, self.BLOB_NAME)
        self.assertEqual(fixedAndVariableParam, readFixedAndVariableParam)

    def testWriteFailureWrongArraySize(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.WRONG_ARRAY_SIZE, self.EXTRA_LIMIT,
                                                                  self.LIMIT, self.COLOR, self.ACCESS,
                                                                  self.FLOAT_VALUE)

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def testWriteFailureWrongExtraLimit(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        fixedAndVariableParam.extra_limit = self.WRONG_EXTRA_LIMIT

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def testWriteFailureWrongLimitHolder(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        limitHolder = self.api.LimitHolder(self.LIMIT)
        fixedAndVariableParam.limit_holder = limitHolder

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def testWriteFailureWrongColor(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        fixedAndVariableParam.color = self.WRONG_COLOR

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def testWriteFailureWrongAccess(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        fixedAndVariableParam.access = self.WRONG_ACCESS

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def testWriteFailureWrongFloatValue(self):
        fixedAndVariableParam = self._createFixedAndVariableParam(self.ARRAY_SIZE, self.EXTRA_LIMIT, self.LIMIT,
                                                                  self.COLOR, self.ACCESS, self.FLOAT_VALUE)
        fixedAndVariableParam.float_value = self.WRONG_FLOAT_VALUE

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(fixedAndVariableParam)

    def _createArrayHolder(self, size, extraLimit, limitHolder, color, access, floatValue):
        array = []
        for i in range(size):
            array.append(i * i)
        hasBlack = color == self.api.Color.BLACK
        hasRead = (access & self.api.Access.Values.READ) == self.api.Access.Values.READ
        hasFloatBiggerThanOne = floatValue > 1.0

        return self.api.ArrayHolder(size, extraLimit, limitHolder, color, access, floatValue, array,
                                    self.EXTRA_VALUE, hasBlack, hasRead, hasFloatBiggerThanOne)

    def _createFixedAndVariableParam(self, size, extraLimit, limit, color, access, floatValue):
        limitHolder = self.api.LimitHolder(limit)
        arrayHolder = self._createArrayHolder(size, extraLimit, limitHolder, color, access, floatValue)

        return self.api.FixedAndVariableParam(extraLimit, limitHolder, color, access, floatValue, arrayHolder)

    def _checkArrayHolderInStream(self, reader, arrayHolder, size, extraLimit, limitHolder, color, access,
                                  floatValue):
        self.assertEqual(arrayHolder.size, size)
        self.assertEqual(arrayHolder.extra_limit, extraLimit)
        self.assertEqual(arrayHolder.limit_holder, limitHolder)
        self.assertEqual(arrayHolder.color, color)
        self.assertEqual(arrayHolder.access, access)
        self.assertEqual(arrayHolder.float_value, floatValue)

        for i in range(size):
            self.assertEqual(arrayHolder.array[i], reader.read_varuint())
        self.assertEqual(arrayHolder.extra_value, reader.read_bits(3))

    def _checkFixedAndVariableParamInStream(self, reader, fixedAndVariableParam, size, extraLimit, limit,
                                            color, access, floatValue):
        self.assertEqual(extraLimit, reader.read_bits(8))
        self.assertEqual(limit, reader.read_bits(8))
        self.assertEqual(color.value, reader.read_bits(2))
        self.assertEqual(access.value, reader.read_bits(4))
        self.assertEqual(floatValue, reader.read_float16())
        arrayHolder = fixedAndVariableParam.array_holder
        limitHolder = fixedAndVariableParam.limit_holder
        self._checkArrayHolderInStream(reader, arrayHolder, size, extraLimit, limitHolder, color, access,
                                       floatValue)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "fixed_and_variable_param.blob")

    ARRAY_SIZE = 1000
    WRONG_ARRAY_SIZE = 1001
    EXTRA_VALUE = 0x05
    EXTRA_LIMIT = 0x05
    WRONG_EXTRA_LIMIT = 0x06
    LIMIT = 0x06
    FLOAT_VALUE = 2.0
    WRONG_FLOAT_VALUE = 1.0
