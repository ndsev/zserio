import zserio

import OptionalMembers


class OptionalEnumerationTest(OptionalMembers.TestCase):
    def testConstructor(self):
        container = self.api.Container()
        self.assertEqual(None, container.basic_color)

        container = self.api.Container(self.api.BasicColor.WHITE)
        self.assertEqual(self.api.BasicColor.WHITE, container.basic_color)

    def testEq(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertTrue(container1 == container2)

        container1.basic_color = self.api.BasicColor.WHITE
        self.assertFalse(container1 == container2)

        container2.basic_color = self.api.BasicColor.BLACK
        self.assertFalse(container1 == container2)

    def testHash(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertEqual(hash(container1), hash(container2))

        container1.basic_color = self.api.BasicColor.WHITE
        self.assertTrue(hash(container1) != hash(container2))

        container2.basic_color = self.api.BasicColor.BLACK
        self.assertTrue(hash(container1) != hash(container2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(1703, container1.__hash__())
        self.assertEqual(1702, container2.__hash__())

    def testIsBasicColorSetAndUsed(self):
        container = self.api.Container()
        self.assertFalse(container.is_basic_color_set())
        self.assertFalse(container.is_basic_color_used())

        container.basic_color = self.api.BasicColor.WHITE
        self.assertTrue(container.is_basic_color_set())
        self.assertTrue(container.is_basic_color_used())
        self.assertEqual(self.api.BasicColor.WHITE, container.basic_color)

    def testResetBasicColor(self):
        container = self.api.Container(self.api.BasicColor.WHITE)
        self.assertTrue(container.is_basic_color_set())
        self.assertTrue(container.is_basic_color_used())

        container.reset_basic_color()
        self.assertFalse(container.is_basic_color_set())
        self.assertFalse(container.is_basic_color_used())
        self.assertEqual(None, container.basic_color)

    def testBitSizeOf(self):
        container = self.api.Container()
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitsizeof())

        container.basic_color = self.api.BasicColor.WHITE
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitsizeof())

    def testInitializeOffsets(self):
        container = self.api.Container()
        bitPosition = 1
        self.assertEqual(
            bitPosition + self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initialize_offsets(bitPosition)
        )

        container.basic_color = self.api.BasicColor.WHITE
        self.assertEqual(
            bitPosition + self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initialize_offsets(bitPosition)
        )

    def testWrite(self):
        container = self.api.Container()
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStreamWithoutOptional(reader)
        reader.bitposition = 0
        readContainer = self.api.Container.from_reader(reader)
        self.assertFalse(readContainer.is_basic_color_set())
        self.assertFalse(readContainer.is_basic_color_used())

        container.basic_color = self.api.BasicColor.WHITE
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStreamWithOptional(reader, self.api.BasicColor.WHITE)
        reader.bitposition = 0
        readContainer = self.api.Container.from_reader(reader)
        self.assertEqual(self.api.BasicColor.WHITE, readContainer.basic_color)
        self.assertTrue(readContainer.is_basic_color_set())
        self.assertTrue(readContainer.is_basic_color_used())

    def _checkContainerInStreamWithoutOptional(self, reader):
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, reader.buffer_bitsize)
        self.assertEqual(0, reader.read_bits(1))

    def _checkContainerInStreamWithOptional(self, reader, basicColor):
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, reader.buffer_bitsize)
        self.assertEqual(1, reader.read_bits(1))
        self.assertEqual(basicColor.value, reader.read_bits(8))

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 1
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 1 + 8
