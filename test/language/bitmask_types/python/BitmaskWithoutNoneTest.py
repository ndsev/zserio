import unittest

import zserio

from testutils import getZserioApi

class BitmaskWithoutNoneTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "bitmask_types.zs").bitmask_without_none

    def testEmptyConstructor(self):
        permission = self.api.Permission()
        self.assertEqual(0, permission.value)

    def testFromValue(self):
        permission = self.api.Permission.from_value(WRITE_VALUE)
        self.assertTrue((permission & self.api.Permission.Values.WRITE) == self.api.Permission.Values.WRITE)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(-1)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(1 << PERMISSION_BITSIZEOF)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(WRITE_VALUE, PERMISSION_BITSIZEOF)
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
        none = self.api.Permission()
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(hash(read), hash(self.api.Permission.Values.READ))
        self.assertEqual(hash(read), hash(self.api.Permission.from_value(READ_VALUE)))
        self.assertEqual(hash(write), hash(self.api.Permission.Values.WRITE))
        self.assertEqual(hash(write), hash(self.api.Permission.from_value(WRITE_VALUE)))
        self.assertNotEqual(hash(read), hash(write))
        self.assertNotEqual(hash(read), hash(none))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(853, self.api.Permission.Values.READ.__hash__())
        self.assertEqual(855, self.api.Permission.Values.WRITE.__hash__())

    def testStr(self):
        none = self.api.Permission()
        self.assertEqual("0[]", str(none))
        self.assertEqual("2[READ]", str(self.api.Permission.Values.READ))
        self.assertEqual("4[WRITE]", str(self.api.Permission.Values.WRITE))
        self.assertEqual("6[READ | WRITE]", (
            str(self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)))
        self.assertEqual("7[READ | WRITE]", str(self.api.Permission.from_value(7)))

    def testOr(self):
        none = self.api.Permission()
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(read | write, self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)
        self.assertEqual(read | self.api.Permission.Values.WRITE, self.api.Permission.Values.READ | write)
        self.assertEqual(read, read | none)
        self.assertEqual(write, none | write)

        self.assertEqual(READ_VALUE | WRITE_VALUE, (read | write).value)

    def testAnd(self):
        none = self.api.Permission()
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE
        readwrite = self.api.Permission.Values.READ | self.api.Permission.Values.WRITE

        self.assertEqual(read, readwrite & read)
        self.assertEqual(write, readwrite & write)
        self.assertEqual(none, readwrite & none)
        self.assertEqual(none, read & none)
        self.assertEqual(none, write & none)
        self.assertEqual(none, read & write)
        self.assertEqual(read, read & read & read & read & read)

    def testXor(self):
        none = self.api.Permission()
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(read ^ write, self.api.Permission.Values.READ ^ self.api.Permission.Values.WRITE)
        self.assertEqual(READ_VALUE ^ WRITE_VALUE, (read ^ write).value)
        self.assertEqual(read, (read ^ write) & read)
        self.assertEqual(write, (read ^ write) & write)
        self.assertEqual(none, read ^ read)
        self.assertEqual(none, write ^ write)

    def testInvert(self): # bitwise not operator
        none = self.api.Permission()
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(write, ~read & write)
        self.assertEqual(none, ~read & read)
        self.assertEqual(write, ~none & write)
        self.assertEqual(read, ~none & read)
        self.assertEqual(read | write, ~none & (read | write))

    def testBitSizeOf(self):
        none = self.api.Permission()
        self.assertEqual(PERMISSION_BITSIZEOF, none.bitsizeof())
        self.assertEqual(PERMISSION_BITSIZEOF, none.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        self.assertEqual(bitPosition + PERMISSION_BITSIZEOF,
                         self.api.Permission.Values.READ.initialize_offsets(bitPosition))

    def testWrite(self):
        permission = self.api.Permission.Values.READ
        writer = zserio.BitStreamWriter()
        permission.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readPermission = self.api.Permission.from_reader(reader)
        self.assertEqual(permission, readPermission)

    def testGetValue(self):
        self.assertEqual(READ_VALUE, self.api.Permission.Values.READ.value)
        self.assertEqual(WRITE_VALUE, self.api.Permission.Values.WRITE.value)
        self.assertEqual(READ_VALUE | WRITE_VALUE,
                         (self.api.Permission.Values.READ | self.api.Permission.Values.WRITE).value)

PERMISSION_BITSIZEOF = 3

READ_VALUE = 2
WRITE_VALUE = 4
