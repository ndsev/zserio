import unittest
import zserio

from testutils import getZserioApi

class OptionalExpressionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_expression

    def testEmptyConstructor(self):
        container1 = self.api.Container()
        self.assertEqual(None, container1.getBasicColor())
        self.assertEqual(None, container1.getNumBlackTones())
        self.assertEqual(None, container1.getBlackColor())

    def testFromFields(self):
        container1 = self.api.Container.fromFields(self.api.BasicColor.BLACK, self.NUM_BLACK_TONES, None)
        self.assertEqual(self.api.BasicColor.BLACK, container1.getBasicColor())
        self.assertEqual(self.NUM_BLACK_TONES, container1.getNumBlackTones())
        self.assertEqual(None, container1.getBlackColor())

    def testEq(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertTrue(container1 == container2)

        container1.setBasicColor(self.api.BasicColor.WHITE)
        container2.setBasicColor(self.api.BasicColor.BLACK)
        container2.setNumBlackTones(self.NUM_BLACK_TONES)
        container2.setBlackColor(self._createBlackColor(self.NUM_BLACK_TONES))
        self.assertFalse(container1 == container2)

        container2.setBasicColor(self.api.BasicColor.WHITE)
        container2.setNumBlackTones(None)
        container2.setBlackColor(None)
        self.assertTrue(container1 == container2)

    def testHash(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertEqual(hash(container1), hash(container2))

        container1.setBasicColor(self.api.BasicColor.WHITE)
        container2.setBasicColor(self.api.BasicColor.BLACK)
        container2.setNumBlackTones(self.NUM_BLACK_TONES)
        container2.setBlackColor(self._createBlackColor(self.NUM_BLACK_TONES))
        self.assertTrue(hash(container1) != hash(container2))

        container2.setBasicColor(self.api.BasicColor.WHITE)
        container2.setNumBlackTones(None)
        container2.setBlackColor(None)
        self.assertEqual(hash(container1), hash(container2))

    def testHasNumBlackTones(self):
        container = self.api.Container()
        container.setBasicColor(self.api.BasicColor.WHITE)
        self.assertFalse(container.hasNumBlackTones())

        container.setBasicColor(self.api.BasicColor.BLACK)
        container.setNumBlackTones(self.NUM_BLACK_TONES)
        self.assertTrue(container.hasNumBlackTones())
        self.assertEqual(self.NUM_BLACK_TONES, container.getNumBlackTones())

    def testHasBlackColor(self):
        container = self.api.Container()
        container.setBasicColor(self.api.BasicColor.WHITE)
        self.assertFalse(container.hasBlackColor())

        container.setBasicColor(self.api.BasicColor.BLACK)
        blackColor = self._createBlackColor(self.NUM_BLACK_TONES)
        container.setBlackColor(blackColor)
        self.assertTrue(container.hasBlackColor())
        self.assertTrue(blackColor == container.getBlackColor())

    def testBitSizeOf(self):
        container = self.api.Container()
        container.setBasicColor(self.api.BasicColor.WHITE)
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf())

        container.setBasicColor(self.api.BasicColor.BLACK)
        container.setNumBlackTones(self.NUM_BLACK_TONES)
        container.setBlackColor(self._createBlackColor(self.NUM_BLACK_TONES))
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf())

    def testInitializeOffsets(self):
        container = self.api.Container()
        container.setBasicColor(self.api.BasicColor.WHITE)
        bitPosition = 1
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL,
                         container.initializeOffsets(bitPosition))

        container.setBasicColor(self.api.BasicColor.BLACK)
        container.setNumBlackTones(self.NUM_BLACK_TONES)
        container.setBlackColor(self._createBlackColor(self.NUM_BLACK_TONES))
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITH_OPTIONAL,
                         container.initializeOffsets(bitPosition))

    def testWrite(self):
        container = self.api.Container()
        container.setBasicColor(self.api.BasicColor.WHITE)
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkContainerInStream(reader, self.api.BasicColor.WHITE, self.NUM_BLACK_TONES)
        reader.setBitPosition(0)
        readContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.api.BasicColor.WHITE, readContainer.getBasicColor())
        self.assertFalse(readContainer.hasNumBlackTones())
        self.assertFalse(readContainer.hasBlackColor())

        container.setBasicColor(self.api.BasicColor.BLACK)
        container.setNumBlackTones(self.NUM_BLACK_TONES)
        blackColor = self._createBlackColor(self.NUM_BLACK_TONES)
        container.setBlackColor(blackColor)
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkContainerInStream(reader, self.api.BasicColor.BLACK, self.NUM_BLACK_TONES)
        reader.setBitPosition(0)
        readContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.api.BasicColor.BLACK, readContainer.getBasicColor())
        self.assertEqual(self.NUM_BLACK_TONES, readContainer.getNumBlackTones())
        self.assertTrue(blackColor == readContainer.getBlackColor())
        self.assertTrue(readContainer.hasNumBlackTones())
        self.assertTrue(readContainer.hasBlackColor())

    def _createBlackColor(self, numBlackTones):
        return self.api.BlackColor.fromFields(numBlackTones, [i + 1 for i in range(numBlackTones)])

    def _checkContainerInStream(self, reader, basicColor, numBlackTones):
        self.assertEqual(basicColor.value, reader.readBits(8))
        if basicColor == self.api.BasicColor.BLACK:
            self.assertEqual(numBlackTones, reader.readBits(8))
            for i in range(numBlackTones):
                self.assertEqual(i + 1, reader.readSignedBits(32))

    NUM_BLACK_TONES = 2

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 8
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 8 + 8 + 32 * 2
