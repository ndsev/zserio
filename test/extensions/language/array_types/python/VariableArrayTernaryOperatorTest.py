import os
import zserio

import ArrayTypes

from testutils import getApiDir


class VariableArrayTernaryOperatorTest(ArrayTypes.TestCase):
    def testFirstWriteReadFile(self):
        isFirstOffsetUsed = True
        self._testWriteReadFile(isFirstOffsetUsed)

    def testSecondWriteReadFile(self):
        isFirstOffsetUsed = False
        self._testWriteReadFile(isFirstOffsetUsed)

    def _createVariableArray(self, isFirstSizeUsed):
        variableArray = self.api.VariableArray()
        variableArray.is_first_size_used = isFirstSizeUsed
        currentSize = variableArray.first_size if isFirstSizeUsed else variableArray.second_size
        array = []
        for i in range(currentSize * currentSize):
            variableArrayElement = self.api.VariableArrayElement(currentSize)
            variableArrayElement.element = i
            array.append(variableArrayElement)
        variableArray.array = array

        return variableArray

    def _checkVariableArray(self, variableArray, isFirstSizeUsed):
        self.assertEqual(isFirstSizeUsed, variableArray.is_first_size_used)
        currentSize = self.FIRST_SIZE if isFirstSizeUsed else self.SECOND_SIZE
        array = variableArray.array
        for i in range(currentSize * currentSize):
            variableArrayElement = array[i]
            self.assertEqual(currentSize, variableArrayElement.bit_size)
            self.assertEqual(i, variableArrayElement.element)

    def _testWriteReadFile(self, isFirstSizeUsed):
        variableArray = self._createVariableArray(isFirstSizeUsed)
        blobName = self.BLOB_NAME_FIRST if isFirstSizeUsed else self.BLOB_NAME_SECOND
        zserio.serialize_to_file(variableArray, blobName)

        readVariableArray = zserio.deserialize_from_file(self.api.VariableArray, blobName)
        self._checkVariableArray(readVariableArray, isFirstSizeUsed)
        self.assertEqual(variableArray, readVariableArray)

    FIRST_SIZE = 10
    SECOND_SIZE = 20

    BLOB_NAME_FIRST = os.path.join(
        getApiDir(os.path.dirname(__file__)), "variable_array_ternary_operator1.blob"
    )
    BLOB_NAME_SECOND = os.path.join(
        getApiDir(os.path.dirname(__file__)), "variable_array_ternary_operator2.blob"
    )
