"""
The module implements abstraction for writing data to the bit stream.
"""

from zserio.bitsizeof import (getBitSizeOfVarInt16, getBitSizeOfVarInt32,
                              getBitSizeOfVarInt64, getBitSizeOfVarInt,
                              getBitSizeOfVarUInt16, getBitSizeOfVarUInt32,
                              getBitSizeOfVarUInt64, getBitSizeOfVarUInt,
                              INT64_MIN)
from zserio.exception import PythonRuntimeException
from zserio.float import convertFloatToUInt16, convertFloatToUInt32, convertFloatToUInt64

class BitStreamWriter:
    """
    Bit stream writer using bytearray.
    """

    def __init__(self):
        """
        Constructor.
        """

        self._byteArray = bytearray()
        self._bitPosition = 0

    def writeBits(self, value, numBits):
        """
        Writes the given value with the given number of bits to the underlying storage.

        :param value: Value to write.
        :param numBits: Number of bits to write.
        :raises PythonRuntimeException: If the value is out of the range or if the number of bits is invalid.
        """

        if numBits <= 0:
            raise PythonRuntimeException("BitStreamWriter.writeBits: numBits '%d' is less than 1!" % numBits)

        minValue = 0
        maxValue = (1 << numBits) - 1
        if value < minValue or value > maxValue:
            raise PythonRuntimeException("BitStreamWriter.writeBits: Value '%d' is out of the range <%d,%d>!" %
                                         (value, minValue, maxValue))

        self._writeBitsImpl(value, numBits, signed=False)

    def writeSignedBits(self, value, numBits):
        """
        Writes the given signed value with the given number of bits to the underlying storage.
        Provided for convenience.

        :param value: Signed value to write.
        :param numBits: Number of bits to write.
        :raises PythonRuntimeException: If the value is out of the range or if the number of bits is invalid.
        """

        if numBits <= 0:
            raise PythonRuntimeException("BitStreamWriter.writeSignedBits: numBits '%d' is less than 1!" %
                                         numBits)

        minValue = -(1 << (numBits - 1))
        maxValue = (1 << (numBits - 1)) - 1
        if value < minValue or value > maxValue:
            raise PythonRuntimeException("BitStreamWriter.writeSignedBits: "
                                         "Value '%d' is out of the range <%d,%d>!" %
                                         (value, minValue, maxValue))

        self._writeBitsImpl(value, numBits, signed=True)

    def writeVarInt16(self, value):
        """
        Writes a variable 16-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarInt16(value) // 8
        self._writeVarNum(abs(value), numBytes, VARINT16_NUM_BITS, sign=(value < 0))

    def writeVarInt32(self, value):
        """
        Writes a variable 32-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarInt32(value) // 8
        self._writeVarNum(abs(value), numBytes, VARINT32_NUM_BITS, sign=(value < 0))

    def writeVarInt64(self, value):
        """
        Writes a variable 16-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarInt64(value) // 8
        self._writeVarNum(abs(value), numBytes, VARINT64_NUM_BITS, sign=(value < 0))

    def writeVarInt(self, value):
        """
        Writes a variable signed integer value (up to 9 bytes) to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        if value == INT64_MIN:
            self.writeBits(0x80, 8) # INT64_MIN is stored as -0
        else:
            numBytes = getBitSizeOfVarInt(value) // 8
            self._writeVarNum(abs(value), numBytes, VARINT_NUM_BITS, sign=(value < 0))

    def writeVarUInt16(self, value):
        """
        Writes a variable 16-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarUInt16(value) // 8
        self._writeVarNum(value, numBytes, VARUINT16_NUM_BITS)

    def writeVarUInt32(self, value):
        """
        Writes a variable 32-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarUInt32(value) // 8
        self._writeVarNum(value, numBytes, VARUINT32_NUM_BITS)

    def writeVarUInt64(self, value):
        """
        Writes a variable 16-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarUInt64(value) // 8
        self._writeVarNum(value, numBytes, VARUINT64_NUM_BITS)

    def writeVarUInt(self, value):
        """
        Writes a variable unsigned integer value (up to 9 bytes) to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        numBytes = getBitSizeOfVarUInt(value) // 8
        self._writeVarNum(value, numBytes, VARUINT_NUM_BITS)

    def writeFloat16(self, value):
        """
        Writes a 16-bit float value to the underlying storage according to IEEE 754 binary16.

        :param value: Float value to write.
        """

        self.writeBits(convertFloatToUInt16(value), 16)

    def writeFloat32(self, value):
        """
        Writes a 32-bit float value to the underlying storage according to IEEE 754 binary32.

        :param value: Float value to write.
        """

        self.writeBits(convertFloatToUInt32(value), 32)

    def writeFloat64(self, value):
        """
        Writes a 64-bit float value to the underlying storage according to IEEE 754 binary64.

        :param value: Float value to write.
        """

        self.writeBits(convertFloatToUInt64(value), 64)

    def writeString(self, string):
        """
        Writes the given string to the underlying storage in UTF-8 encoding. Length of the string is written
        as varuint64 at the beginning.

        :param string: String to write.
        """

        stringBytes = string.encode("utf-8")
        self.writeVarUInt64(len(stringBytes))
        for stringByte in stringBytes:
            self.writeBits(stringByte, 8)

    def writeBool(self, value):
        """
        Writes bool in a single bit.

        :param value: Bool value to write.
        """

        self.writeBits(1 if value else 0, 1)

    def writeBitBuffer(self, bitBuffer):
        """
        Writes a bit buffer to the underlying storage. Length of the bit buffer is written as varuint64
        at the beginning.

        :param bitBuffer: Bit buffer to write.
        """

        bitSize = bitBuffer.getBitSize()
        self.writeVarUInt64(bitSize)

        writeBuffer = bitBuffer.getBuffer()
        numBytesToWrite = bitSize // 8
        numRestBits = bitSize - numBytesToWrite * 8
        beginBitPosition = self._bitPosition
        if (beginBitPosition & 0x07) != 0:
            # we are not aligned to byte
            for i in range(numBytesToWrite):
                self.writeBits(writeBuffer[i], 8)
        else:
            # we are aligned to byte
            self._byteArray += writeBuffer[0:numBytesToWrite]
            self._bitPosition += numBytesToWrite * 8

        if numRestBits > 0:
            self.writeBits(writeBuffer[numBytesToWrite], numRestBits)

    def getByteArray(self):
        """
        Gets internal bytearray.

        :returns: Underlying bytearray object.
        """

        return self._byteArray

    def toFile(self, filename):
        """
        Writes underlying bytearray to binary file.

        :param filename: File to write.
        """

        with open(filename, "wb") as file:
            file.write(self._byteArray)

    def getBitPosition(self):
        """
        Gets current bit position.

        :returns: Current bit position.
        """

        return self._bitPosition

    def alignTo(self, alignment):
        """
        Aligns the bit position according to the aligning value.

        :param alignment: An aligning value to use.
        """

        offset = self._bitPosition % alignment
        if offset != 0:
            self.writeBits(0, alignment - offset)

    def _writeBitsImpl(self, value, numBits, *, signed):
        bufferLastByteBits = self._bitPosition % 8
        bufferFreeBits = (8 - bufferLastByteBits) if bufferLastByteBits != 0 else 0
        valueFirstByteBits = numBits % 8 or 8
        if valueFirstByteBits <= bufferFreeBits:
            leftShift = bufferFreeBits - valueFirstByteBits
        else:
            leftShift = bufferFreeBits + 8 - valueFirstByteBits
        value <<= leftShift
        numBytes = (numBits + leftShift + 7) // 8
        valueBytes = value.to_bytes(numBytes, byteorder='big', signed=signed)
        if bufferFreeBits == 0:
            self._byteArray.extend(valueBytes)
        else:
            valueFirstByte = valueBytes[0] & ((1 << bufferFreeBits) - 1)
            self._byteArray[-1] |= valueFirstByte
            self._byteArray.extend(valueBytes[1:])

        self._bitPosition += numBits

    def _writeVarNum(self, absValue, numBytes, varNumBits, *, sign=False):
        maxNumBytes = len(varNumBits)
        for i in reversed(range(1, numBytes + 1)):
            numBits = varNumBits[numBytes - i]
            if numBits == VAR_NUM_SIGNED_FIRST_BYTE:
                self.writeBool(sign)
            if numBits != VAR_NUM_LAST_BYTE:
                self.writeBool(i > 1) # has next byte
            shiftBits = (i - 1) * 7 + (1 if numBytes == maxNumBytes and i > 1 else 0)
            self.writeBits((absValue >> shiftBits) & VAR_NUM_BIT_MASKS[numBits - 1], numBits)

VAR_NUM_BIT_MASKS = [0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff]
VAR_NUM_SIGNED_FIRST_BYTE = 6
VAR_NUM_LAST_BYTE = 8

VARINT16_NUM_BITS = [6, 8]
VARINT32_NUM_BITS = [6, 7, 7, 8]
VARINT64_NUM_BITS = [6, 7, 7, 7, 7, 7, 7, 8]
VARINT_NUM_BITS = [6, 7, 7, 7, 7, 7, 7, 7, 8]
VARUINT16_NUM_BITS = [7, 8]
VARUINT32_NUM_BITS = [7, 7, 7, 8]
VARUINT64_NUM_BITS = [7, 7, 7, 7, 7, 7, 7, 8]
VARUINT_NUM_BITS = [7, 7, 7, 7, 7, 7, 7, 7, 8]
