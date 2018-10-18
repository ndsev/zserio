from zserio.exception import PythonRuntimeException

class BitStreamWriter:
    """
    Bitstream writer.
    """
    def __init__(self):
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
            raise PythonRuntimeException("BitStreamWriter.writeSignedBits: " +
                                         "Value '%d' is out of the range <%d,%d>!" %
                                         (value, minValue, maxValue))

        self._writeBitsImpl(value, numBits, signed=True)

    def getByteArray(self):
        """
        Gets internal bytearray.

        :returns: Underlying bytearray object.
        """
        return self._byteArray

    def getBitPosition(self):
        """
        Gets current bit position.

        :returns: Current bit position.
        """
        return self._bitPosition

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
