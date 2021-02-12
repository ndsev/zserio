import unittest
import zserio

from testutils import getZserioApi

class AutoArrayOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").auto_array_offset

    def testBitSizeOf(self):
        createWrongOffset = False
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        self.assertEqual(self.AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffset = False
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.AUTO_ARRAY_HOLDER_BIT_SIZE - bitPosition, autoArrayHolder.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffset = True
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        bitPosition = 0
        self.assertEqual(self.AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition))
        self._checkAutoArrayHolder(autoArrayHolder)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffset = True
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition))
        self._checkAutoArrayHolder(autoArrayHolder, bitPosition)

    def testRead(self):
        writeWrongOffset = False
        writer = zserio.BitStreamWriter()
        self._writeAutoArrayHolderToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        autoArrayHolder = self.api.AutoArrayHolder.fromReader(reader)
        self._checkAutoArrayHolder(autoArrayHolder)

    def testReadWrongOffsets(self):
        writeWrongOffset = True
        writer = zserio.BitStreamWriter()
        self._writeAutoArrayHolderToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        with self.assertRaises(zserio.PythonRuntimeException):
            autoArrayHolder = self.api.AutoArrayHolder.fromReader(reader)
            self._checkAutoArrayHolder(autoArrayHolder)

    def testWrite(self):
        createWrongOffset = True
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        writer = zserio.BitStreamWriter()
        autoArrayHolder.write(writer)
        self._checkAutoArrayHolder(autoArrayHolder)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readAutoArrayHolder = self.api.AutoArrayHolder.fromReader(reader)
        self._checkAutoArrayHolder(readAutoArrayHolder)
        self.assertTrue(autoArrayHolder == readAutoArrayHolder)

    def testWriteWithPosition(self):
        createWrongOffset = True
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.writeBits(0, bitPosition)
        autoArrayHolder.write(writer)
        self._checkAutoArrayHolder(autoArrayHolder, bitPosition)

    def testWriteWrongOffset(self):
        createWrongOffset = True
        autoArrayHolder = self._createAutoArrayHolder(createWrongOffset)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            autoArrayHolder.write(writer, callInitializeOffsets=False)

    def _writeAutoArrayHolderToStream(self, writer, writeWrongOffset):
        writer.writeBits(self.WRONG_AUTO_ARRAY_OFFSET if writeWrongOffset else self.AUTO_ARRAY_OFFSET, 32)
        writer.writeBits(self.FORCED_ALIGNMENT_VALUE, 8)
        writer.writeVarSize(self.AUTO_ARRAY_LENGTH)
        for i in range(self.AUTO_ARRAY_LENGTH):
            writer.writeBits(i, 7)

    def _checkAutoArrayHolder(self, autoArrayHolder, bitPosition=0):
        expectedAutoArrayOffset = (self.AUTO_ARRAY_OFFSET if (bitPosition == 0) else
                                   self.AUTO_ARRAY_OFFSET + (bitPosition // 8))
        self.assertEqual(expectedAutoArrayOffset, autoArrayHolder.getAutoArrayOffset())

        self.assertEqual(self.FORCED_ALIGNMENT_VALUE, autoArrayHolder.getForceAlignment())

        autoArray = autoArrayHolder.getAutoArray()
        self.assertEqual(self.AUTO_ARRAY_LENGTH, len(autoArray))
        for i in range(self.AUTO_ARRAY_LENGTH):
            self.assertEqual(i, autoArray[i])

    def _createAutoArrayHolder(self, createWrongOffset):
        autoArrayOffset = self.WRONG_AUTO_ARRAY_OFFSET if createWrongOffset else self.AUTO_ARRAY_OFFSET
        autoArray = list(range(self.AUTO_ARRAY_LENGTH))

        return self.api.AutoArrayHolder(autoArrayOffset, self.FORCED_ALIGNMENT_VALUE, autoArray)

    AUTO_ARRAY_LENGTH = 5
    FORCED_ALIGNMENT_VALUE = 0

    WRONG_AUTO_ARRAY_OFFSET = 0
    AUTO_ARRAY_OFFSET = 5

    AUTO_ARRAY_HOLDER_BIT_SIZE = 32 + 1 + 7 + 8 + 5 * 7
