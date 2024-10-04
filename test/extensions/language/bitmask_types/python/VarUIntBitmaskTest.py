import os
import zserio

import BitmaskTypes

from testutils import getApiDir


class VarUIntBitmaskTest(BitmaskTypes.TestCase):
    def testEmptyConstructor(self):
        permission = self.api.Permission()
        self.assertEqual(0, permission.value)

    def testFromValue(self):
        permission = self.api.Permission.from_value(WRITE_VALUE)
        self.assertTrue((permission & self.api.Permission.Values.WRITE) == self.api.Permission.Values.WRITE)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(-1)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(1 << 64)  # more that UINT64_MAX

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(WRITE_VALUE, zserio.bitsizeof.bitsizeof_varuint(WRITE_VALUE))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        permission = self.api.Permission.from_reader(reader)
        self.assertEqual(self.api.Permission.Values.WRITE, permission)

    def testEq(self):
        self.assertTrue(self.api.Permission.Values.READ == self.api.Permission.Values.READ)
        self.assertFalse(self.api.Permission.Values.READ == self.api.Permission.Values.WRITE)
        self.assertTrue(self.api.Permission.Values.WRITE == self.api.Permission.Values.WRITE)

        read = self.api.Permission.Values.READ
        self.assertTrue(read == self.api.Permission.Values.READ)
        self.assertTrue(self.api.Permission.Values.READ == read)
        self.assertFalse(read == self.api.Permission.Values.WRITE)
        self.assertFalse(self.api.Permission.Values.WRITE == read)

        write = self.api.Permission.Values.WRITE
        self.assertTrue(write == self.api.Permission.Values.WRITE)
        self.assertTrue(self.api.Permission.Values.WRITE == write)
        self.assertFalse(write == self.api.Permission.Values.READ)
        self.assertFalse(self.api.Permission.Values.READ == write)

        self.assertTrue(read == self.api.Permission.from_value(read.value))  # copy
        self.assertTrue(write == self.api.Permission.from_value(write.value))  # copy

        self.assertFalse(read == write)

    def testHash(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(hash(read), hash(self.api.Permission.Values.READ))
        self.assertEqual(hash(read), hash(self.api.Permission.from_value(READ_VALUE)))
        self.assertEqual(hash(write), hash(self.api.Permission.Values.WRITE))
        self.assertEqual(hash(write), hash(self.api.Permission.from_value(WRITE_VALUE)))
        self.assertNotEqual(hash(read), hash(write))
        self.assertNotEqual(hash(read), hash(self.api.Permission.Values.NONE))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(851, self.api.Permission.Values.NONE.__hash__())
        self.assertEqual(853, self.api.Permission.Values.READ.__hash__())
        self.assertEqual(855, self.api.Permission.Values.WRITE.__hash__())

    def testStr(self):
        self.assertEqual("0[NONE]", str(self.api.Permission.Values.NONE))
        self.assertEqual("2[READ]", str(self.api.Permission.Values.READ))
        self.assertEqual("4[WRITE]", str(self.api.Permission.Values.WRITE))
        self.assertEqual(
            "6[READ | WRITE]", (str(self.api.Permission.Values.READ | self.api.Permission.Values.WRITE))
        )
        self.assertEqual("7[READ | WRITE]", str(self.api.Permission.from_value(7)))
        self.assertEqual("255[READ | WRITE]", str(self.api.Permission.from_value(255)))

    def testOr(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(read | write, self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)
        self.assertEqual(read | self.api.Permission.Values.WRITE, self.api.Permission.Values.READ | write)
        self.assertEqual(read, read | self.api.Permission.Values.NONE)
        self.assertEqual(write, self.api.Permission.Values.NONE | write)

        self.assertEqual(READ_VALUE | WRITE_VALUE, (read | write).value)

    def testAnd(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE
        readwrite = self.api.Permission.Values.READ | self.api.Permission.Values.WRITE

        self.assertEqual(read, readwrite & read)
        self.assertEqual(write, readwrite & write)
        self.assertEqual(self.api.Permission.Values.NONE, readwrite & self.api.Permission.Values.NONE)
        self.assertEqual(self.api.Permission.Values.NONE, read & self.api.Permission.Values.NONE)
        self.assertEqual(self.api.Permission.Values.NONE, write & self.api.Permission.Values.NONE)
        self.assertEqual(self.api.Permission.Values.NONE, read & write)
        self.assertEqual(read, read & read & read & read & read)

    def testXor(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(read ^ write, self.api.Permission.Values.READ ^ self.api.Permission.Values.WRITE)
        self.assertEqual(READ_VALUE ^ WRITE_VALUE, (read ^ write).value)
        self.assertEqual(read, (read ^ write) & read)
        self.assertEqual(write, (read ^ write) & write)
        self.assertEqual(self.api.Permission.Values.NONE, read ^ read)
        self.assertEqual(self.api.Permission.Values.NONE, write ^ write)

    def testInvert(self):  # bitwise not operator
        none = self.api.Permission.Values.NONE
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(write, ~read & write)
        self.assertEqual(none, ~read & read)
        self.assertEqual(write, ~none & write)
        self.assertEqual(read, ~none & read)
        self.assertEqual(read | write, ~none & (read | write))

    def testBitSizeOf(self):
        self.assertEqual(
            zserio.bitsizeof.bitsizeof_varuint(NONE_VALUE), self.api.Permission.Values.NONE.bitsizeof()
        )
        self.assertEqual(
            zserio.bitsizeof.bitsizeof_varuint(NONE_VALUE), self.api.Permission.Values.NONE.bitsizeof(1)
        )

    def testInitializeOffsets(self):
        bitPosition = 1
        self.assertEqual(
            bitPosition + zserio.bitsizeof.bitsizeof_varuint(READ_VALUE),
            self.api.Permission.Values.READ.initialize_offsets(bitPosition),
        )

    def testWriteRead(self):
        permission = self.api.Permission.Values.READ
        writer = zserio.BitStreamWriter()
        permission.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readPermission = self.api.Permission.from_reader(reader)
        self.assertEqual(permission, readPermission)

    def testWriteReadFile(self):
        permission = self.api.Permission.Values.READ
        zserio.serialize_to_file(permission, BLOB_NAME)

        readPermission = zserio.deserialize_from_file(self.api.Permission, BLOB_NAME)
        self.assertEqual(permission, readPermission)

    def testGetValue(self):
        self.assertEqual(NONE_VALUE, self.api.Permission.Values.NONE.value)
        self.assertEqual(READ_VALUE, self.api.Permission.Values.READ.value)
        self.assertEqual(WRITE_VALUE, self.api.Permission.Values.WRITE.value)


BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "varuint_bitmask.blob")

NONE_VALUE = 0
READ_VALUE = 2
WRITE_VALUE = 4
