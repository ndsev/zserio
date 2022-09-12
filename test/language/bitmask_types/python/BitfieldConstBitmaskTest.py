import unittest

import zserio

from testutils import getZserioApi

class BitfieldConstBitmaskTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "bitmask_types.zs").bitfield_const_bitmask
        cls.PERMISSION_BITSIZEOF = cls.api.NUM_BITS

    def testEmptyConstructor(self):
        permission = self.api.Permission()
        self.assertEqual(0, permission.value)

    def testFromValue(self):
        permission = self.api.Permission.from_value(WRITE_VALUE)
        self.assertTrue((permission & self.api.Permission.Values.WRITE) == self.api.Permission.Values.WRITE)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(-1)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(1 << self.PERMISSION_BITSIZEOF)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(WRITE_VALUE, self.PERMISSION_BITSIZEOF)
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

        self.assertTrue(read == self.api.Permission.from_value(read.value)) # copy
        self.assertTrue(write == self.api.Permission.from_value(write.value)) # copy

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
        self.assertEqual(851, hash(self.api.Permission.Values.NONE))
        self.assertEqual(853, hash(self.api.Permission.Values.READ))
        self.assertEqual(855, hash(self.api.Permission.Values.WRITE))

    def testStr(self):
        self.assertEqual("0[NONE]", str(self.api.Permission.Values.NONE))
        self.assertEqual("2[READ]", str(self.api.Permission.Values.READ))
        self.assertEqual("4[WRITE]", str(self.api.Permission.Values.WRITE))
        self.assertEqual("6[READ | WRITE]", (
            str(self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)))
        self.assertEqual("7[READ | WRITE]", str(self.api.Permission.from_value(7)))

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

    def testInvert(self): # bitwise not operator
        none = self.api.Permission.Values.NONE
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(write, ~read & write)
        self.assertEqual(none, ~read & read)
        self.assertEqual(write, ~none & write)
        self.assertEqual(read, ~none & read)
        self.assertEqual(read | write, ~none & (read | write))

    def testBitSizeOf(self):
        self.assertEqual(self.PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE.bitsizeof())
        self.assertEqual(self.PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        self.assertEqual(bitPosition + self.PERMISSION_BITSIZEOF,
                         self.api.Permission.Values.READ.initialize_offsets(bitPosition))

    def testWrite(self):
        permission = self.api.Permission.Values.READ
        writer = zserio.BitStreamWriter()
        permission.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readPermission = self.api.Permission.from_reader(reader)
        self.assertEqual(permission, readPermission)

    def testGetValue(self):
        self.assertEqual(NONE_VALUE, self.api.Permission.Values.NONE.value)
        self.assertEqual(READ_VALUE, self.api.Permission.Values.READ.value)
        self.assertEqual(WRITE_VALUE, self.api.Permission.Values.WRITE.value)

NONE_VALUE = 0
READ_VALUE = 2
WRITE_VALUE = 4
