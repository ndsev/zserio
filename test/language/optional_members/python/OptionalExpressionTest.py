import unittest
import zserio

from testutils import getZserioApi

class OptionalExpressionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_expression

    def testConstructor(self):
        container = self.api.Container()
        self.assertEqual(None, container.basic_color)
        self.assertEqual(None, container.num_black_tones)
        self.assertEqual(None, container.black_color)

        container = self.api.Container(self.api.BasicColor.BLACK, self.NUM_BLACK_TONES)
        self.assertEqual(self.api.BasicColor.BLACK, container.basic_color)
        self.assertEqual(self.NUM_BLACK_TONES, container.num_black_tones)
        self.assertEqual(None, container.black_color)

        container = self.api.Container(basicColor_=self.api.BasicColor.BLACK,
                                       numBlackTones_=self.NUM_BLACK_TONES)
        self.assertEqual(self.api.BasicColor.BLACK, container.basic_color)
        self.assertEqual(self.NUM_BLACK_TONES, container.num_black_tones)
        self.assertEqual(None, container.black_color)

    def testEq(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertTrue(container1 == container2)

        container1.basic_color = self.api.BasicColor.WHITE
        container2.basic_color = self.api.BasicColor.BLACK
        container2.num_black_tones = self.NUM_BLACK_TONES
        container2.black_color = self._createBlackColor(self.NUM_BLACK_TONES)
        self.assertFalse(container1 == container2)

        container2.basic_color = self.api.BasicColor.WHITE
        container2.num_black_tones = None
        container2.black_color = None
        self.assertTrue(container1 == container2)

    def testHash(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertEqual(hash(container1), hash(container2))

        container1.basic_color = self.api.BasicColor.WHITE
        container2.basic_color = self.api.BasicColor.BLACK
        container2.num_black_tones = self.NUM_BLACK_TONES
        container2.black_color = self._createBlackColor(self.NUM_BLACK_TONES)
        self.assertTrue(hash(container1) != hash(container2))

        container2.basic_color = self.api.BasicColor.WHITE
        container2.num_black_tones = None
        container2.black_color = None
        self.assertEqual(hash(container1), hash(container2))

    def testIsNumBlackTonesUsed(self):
        container = self.api.Container()
        container.basic_color = self.api.BasicColor.WHITE
        self.assertFalse(container.isNumBlackTonesUsed())

        container.basic_color = self.api.BasicColor.BLACK
        container.num_black_tones = self.NUM_BLACK_TONES
        self.assertTrue(container.isNumBlackTonesUsed())
        self.assertEqual(self.NUM_BLACK_TONES, container.num_black_tones)

    def testIsBlackColorUsed(self):
        container = self.api.Container()
        container.basic_color = self.api.BasicColor.WHITE
        self.assertFalse(container.isBlackColorUsed())

        container.basic_color = self.api.BasicColor.BLACK
        blackColor = self._createBlackColor(self.NUM_BLACK_TONES)
        container.black_color = blackColor
        self.assertTrue(container.isBlackColorUsed())
        self.assertTrue(blackColor == container.black_color)

    def testBitSizeOf(self):
        container = self.api.Container()
        container.basic_color = self.api.BasicColor.WHITE
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf())

        container.basic_color = self.api.BasicColor.BLACK
        container.num_black_tones = self.NUM_BLACK_TONES
        container.black_color = self._createBlackColor(self.NUM_BLACK_TONES)
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf())

    def testInitializeOffsets(self):
        container = self.api.Container()
        container.basic_color = self.api.BasicColor.WHITE
        bitPosition = 1
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL,
                         container.initializeOffsets(bitPosition))

        container.basic_color = self.api.BasicColor.BLACK
        container.num_black_tones = self.NUM_BLACK_TONES
        container.black_color = self._createBlackColor(self.NUM_BLACK_TONES)
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITH_OPTIONAL,
                         container.initializeOffsets(bitPosition))

    def testWrite(self):
        container = self.api.Container()
        container.basic_color = self.api.BasicColor.WHITE
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStream(reader, self.api.BasicColor.WHITE, self.NUM_BLACK_TONES)
        reader.bitposition = 0
        readContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.api.BasicColor.WHITE, readContainer.basic_color)
        self.assertFalse(readContainer.isNumBlackTonesUsed())
        self.assertFalse(readContainer.isBlackColorUsed())

        container.basic_color = self.api.BasicColor.BLACK
        container.num_black_tones = self.NUM_BLACK_TONES
        blackColor = self._createBlackColor(self.NUM_BLACK_TONES)
        container.black_color = blackColor
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStream(reader, self.api.BasicColor.BLACK, self.NUM_BLACK_TONES)
        reader.bitposition = 0
        readContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.api.BasicColor.BLACK, readContainer.basic_color)
        self.assertEqual(self.NUM_BLACK_TONES, readContainer.num_black_tones)
        self.assertTrue(blackColor == readContainer.black_color)
        self.assertTrue(readContainer.isNumBlackTonesUsed())
        self.assertTrue(readContainer.isBlackColorUsed())

    def _createBlackColor(self, numBlackTones):
        return self.api.BlackColor(numBlackTones, [i + 1 for i in range(numBlackTones)])

    def _checkContainerInStream(self, reader, basicColor, numBlackTones):
        self.assertEqual(basicColor.value, reader.read_bits(8))
        if basicColor == self.api.BasicColor.BLACK:
            self.assertEqual(numBlackTones, reader.read_bits(8))
            for i in range(numBlackTones):
                self.assertEqual(i + 1, reader.read_signed_bits(32))

    NUM_BLACK_TONES = 2

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 8
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 8 + 8 + 32 * 2
