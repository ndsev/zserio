import unittest
import zserio

from testutils import getZserioApi

class AutoOptionalTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").auto_optional

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

    def testIsAutoOptionalIntUsed(self):
        container = self.api.Container()
        container.non_optional_int = self.NON_OPTIONAL_INT_VALUE
        self.assertFalse(container.is_auto_optional_int_used())

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        self.assertTrue(container.is_auto_optional_int_used())

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
        self.assertFalse(readNonOptionalContainer.is_auto_optional_int_used())

        container.auto_optional_int = self.AUTO_OPTIONAL_INT_VALUE
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkContainerInStream(reader, self.NON_OPTIONAL_INT_VALUE, self.AUTO_OPTIONAL_INT_VALUE)
        reader.bitposition = 0
        readAutoOptionalContainer = self.api.Container.from_reader(reader)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, readAutoOptionalContainer.non_optional_int)
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

    NON_OPTIONAL_INT_VALUE = -0x1EADDEAD
    AUTO_OPTIONAL_INT_VALUE = -0x1EEFBEEF

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32
