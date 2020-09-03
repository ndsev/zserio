import unittest
import zserio

from testutils import getZserioApi

class AutoOptionalTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").auto_optional

    def testEmptyConstructor(self):
        container1 = self.api.Container()
        self.assertEqual(0, container1.getNonOptionalInt())
        self.assertEqual(None, container1.getAutoOptionalInt())

    def testEq(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertTrue(container1 == container2)

        container1.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        container1.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        container2.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        self.assertFalse(container1 == container2)

        container2.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        self.assertTrue(container1 == container2)

    def testHash(self):
        container1 = self.api.Container()
        container2 = self.api.Container()
        self.assertEqual(hash(container1), hash(container2))

        container1.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        container1.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        container2.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        self.assertTrue(hash(container1) != hash(container2))

        container2.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        self.assertEqual(hash(container1), hash(container2))

    def testHasAutoOptionalInt(self):
        container = self.api.Container()
        container.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        self.assertFalse(container.hasAutoOptionalInt())

        container.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        self.assertTrue(container.hasAutoOptionalInt())

    def testBitSizeOf(self):
        container = self.api.Container()
        container.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf())

        container.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        self.assertEqual(self.CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf())

    def testInitializeOffsets(self):
        container = self.api.Container()
        container.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        bitPosition = 1
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL,
                         container.initializeOffsets(bitPosition))

        container.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        self.assertEqual(bitPosition + self.CONTAINER_BIT_SIZE_WITH_OPTIONAL,
                         container.initializeOffsets(bitPosition))

    def testWrite(self):
        container = self.api.Container()
        container.setNonOptionalInt(self.NON_OPTIONAL_INT_VALUE)
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkContainerInStream(reader, self.NON_OPTIONAL_INT_VALUE, None)
        reader.setBitPosition(0)
        readNonOptionalContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, readNonOptionalContainer.getNonOptionalInt())
        self.assertFalse(readNonOptionalContainer.hasAutoOptionalInt())

        container.setAutoOptionalInt(self.AUTO_OPTIONAL_INT_VALUE)
        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkContainerInStream(reader, self.NON_OPTIONAL_INT_VALUE, self.AUTO_OPTIONAL_INT_VALUE)
        reader.setBitPosition(0)
        readAutoOptionalContainer = self.api.Container.fromReader(reader)
        self.assertEqual(self.NON_OPTIONAL_INT_VALUE, readAutoOptionalContainer.getNonOptionalInt())
        self.assertTrue(readAutoOptionalContainer.hasAutoOptionalInt())
        self.assertEqual(self.AUTO_OPTIONAL_INT_VALUE, readAutoOptionalContainer.getAutoOptionalInt())

    def _checkContainerInStream(self, reader, nonOptionalIntValue, autoOptionalIntValue):
        if autoOptionalIntValue is None:
            self.assertEqual(nonOptionalIntValue, reader.readSignedBits(32))
            self.assertEqual(False, reader.readBool())
        else:
            self.assertEqual(nonOptionalIntValue, reader.readSignedBits(32))
            self.assertEqual(True, reader.readBool())
            self.assertEqual(autoOptionalIntValue, reader.readSignedBits(32))

    NON_OPTIONAL_INT_VALUE = -0x1EADDEAD
    AUTO_OPTIONAL_INT_VALUE = -0x1EEFBEEF

    CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1
    CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32
