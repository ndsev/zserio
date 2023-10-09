import zserio

import OptionalMembers

class AutoOptionalTest(OptionalMembers.TestCase):
    def testConstructor(self):
        container = self.api.Container()
        self.assertEqual(0, container.non_optional_int)
        self.assertEqual(None, container.auto_optional_int)

        container = self.api.Container(self.NON_OPTIONAL_INT_VALUE)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, container.non_optional_int)
        self.assertEqual(None, container.auto_optional_int)

        container = self.api.Container(non_optional_int_=self.NON_OPTIONAL_INT_VALUE)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, container.non_optional_int)
        self.assertEqual(None, container.auto_optional_int)

    def testEq(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertTrue(container1 == container2)

        container1.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        container1.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        container2.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        self.assertFalse(container1 == container2)

        container2.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertTrue(container1 == container2)

        container1.reset_auto_optional_int()
        self.assertFalse(container1 == container2)

    def testHash(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertEqual(hash(container1), hash(container2))

        container1.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        container1.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        container2.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        self.assertTrue(hash(container1) != hash(container2))

        container2.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertEqual(hash(container1), hash(container2))

        container1.reset_auto_optional_int()
        self.assertTrue(hash(container1) != hash(container2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(3735937536, container1.__hash__())
        self.assertEqual(3994118383, container2.__hash__())

    def testIsAutoOptionalIntSetAndUsed(self):
        container = self.api.Container()
        container.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        self.assertFalse(container.is_auto_optional_int_set())
        self.assertFalse(container.is_auto_optional_int_used())

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertTrue(container.is_auto_optional_int_set())
        self.assertTrue(container.is_auto_optional_int_used())

    def testResetAutoOptionalInt(self):
        container = self.api.Container()
        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertTrue(container.is_auto_optional_int_set())
        self.assertTrue(container.is_auto_optional_int_used())

        container.reset_auto_optional_int()
        self.assertFalse(container.is_auto_optional_int_set())
        self.assertFalse(container.is_auto_optional_int_used())

    def testBitSizeOf(self):
        container = self.api.Container()
        container.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitsizeof())

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitsizeof())

    def testInitializeOffsets(self):
        container = self.api.Container()
        container.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        bitPosition = 1
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL,
                         container.initialize_offsets(bitPosition))

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITH_OPTIONAL,
                         container.initialize_offsets(bitPosition))

    def testWrite(self):
        container = self.api.Container()
        container.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStream(reader, self.NON_OPTIONAL_INT_VALUE, None)
        reader.bitposition = 0
        readNonOptionalContainer = self.api.Container.from_reader(reader)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, readNonOptionalContainer.non_optional_int)
        self.assertFalse(readNonOptionalContainer.is_auto_optional_int_set())
        self.assertFalse(readNonOptionalContainer.is_auto_optional_int_used())

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStream(reader, self.NON_OPTIONAL_INT_VALUE, self.AUTO_OPTIONAL_INT_VALUE)
        reader.bitposition = 0
        readAutoOptionalContainer = self.api.Container.from_reader(reader)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, readAutoOptionalContainer.non_optional_int)
        self.assertTrue(readAutoOptionalContainer.is_auto_optional_int_set())
        self.assertTrue(readAutoOptionalContainer.is_auto_optional_int_used())
        self.assertEqual(self.AUTO_OPTIONAL_INT_VALUE, readAutoOptionalContainer.auto_optional_int)

    def _checkContainerInStream(self, reader, nonOptionalIntValue, autoOptionalIntValue):
        if autoOptionalIntValue is None:
            self.assertEqual(nonOptionalIntValue, reader.read_signed_bits(32))
            self.assertEqual(False, reader.read_bool())
        else:
            self.assertEqual(nonOptionalIntValue, reader.read_signed_bits(32))
            self.assertEqual(True, reader.read_bool())
            self.assertEqual(autoOptionalIntValue, reader.read_signed_bits(32))

    NON_OPTIONAL_INT_VALUE = 0xDEADDEAD - (1 << 32) # it's negative number in 32-bit signed integer
    AUTO_OPTIONAL_INT_VALUE = 0xBEEFBEEF - (1 << 32) # it's negative number in 32-bit signed integer

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32
