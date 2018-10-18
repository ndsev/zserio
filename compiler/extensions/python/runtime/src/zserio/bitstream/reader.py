from zserio.exception import PythonRuntimeException

class BitStreamReader:
    """
    Bitstream reader.
    """
    def __init__(self, buffer=None, *, filename=None):
        """
        Constructor.

        :param buffer: Buffer to read as a bitstream.
        :param filename: Filename to read as a bitstream.
        """
        if buffer is not None:
            self._initFromBuffer(buffer)
        elif filename is not None:
            self._initFromFile(filename)

    def readBits(self, numBits):
        """
        Reads given number of bits from the bitstream as an unsigned integer.

        :param numBits: Number of bits to read.
        :returns: Read bits as an unsigned integer.
        :raises PythonRuntimeException: TODO
        """
        if numBits < 0:
            raise PythonRuntimeException("BitStreamReader.readBits reading negative number of bits!")

        endBitPosition = self._bitPosition + numBits

        if endBitPosition > self._bitSize:
            raise PythonRuntimeException("BitStreamReader.readBits reading behind the stream!")

        startByte = self._bitPosition // 8
        endByte = (endBitPosition - 1) // 8

        value = int.from_bytes(self._buffer[startByte : endByte + 1], byteorder='big', signed=False)

        lastBits = endBitPosition % 8
        if lastBits != 0:
            value >>= (8 - lastBits)
        value &= (1 << numBits) - 1

        self._bitPosition = endBitPosition

        return value

    def readSignedBits(self, numBits):
        """
        Reads given number of bits from the bitstream as a signed integer.

        :param numBits: Number of bits to read
        :returns: Read bits as a signed integer.
        :raises PythonRuntimeException: If the numBits is invalid number of the reading goes behind the stream.
        """
        value = self.readBits(numBits)

        if numBits != 0 and (value >> (numBits - 1)) != 0:
            # signed
            return value - (1 << numBits)
        else:
            # unsigned
            return value

    def _initFromBuffer(self, buffer):
        self._buffer = bytes(buffer) # does copy only if buffer is not mutable (e.g. bytearray)
        self._bitPosition = 0
        self._bitSize = len(buffer) * 8

    def _initFromFile(self, filename):
        with open(filename, 'rb') as f:
            self._initFromBuffer(f.read())
