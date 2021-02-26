import unittest

import zserio

from testutils import getZserioApi

class BitmaskUsedByBitmaskTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "bitmask_types.zs").bitmask_used_by_bitmask
        cls.NONE_VALUE = 0
        cls.READ_VALUE = cls.api.SimplePermission.Values.READ.value
        cls.WRITE_VALUE = cls.api.SimplePermission.Values.WRITE.value

    def testEmptyConstructor(self):
        permission = self.api.Permission()
        self.assertEqual(0, permission.value)

    def testFromValue(self):
        permission = self.api.Permission.fromValue(self.WRITE_VALUE)
        self.assertTrue((permission & self.api.Permission.Values.WRITE) == self.api.Permission.Values.WRITE)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.fromValue(-1)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.fromValue(1 << PERMISSION_BITSIZEOF)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.writeBits(self.WRITE_VALUE, PERMISSION_BITSIZEOF)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        permission = self.api.Permission.fromReader(reader)
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

        self.assertTrue(read == self.api.Permission.fromValue(read.value)) # copy
        self.assertTrue(write == self.api.Permission.fromValue(write.value)) # copy

        self.assertFalse(read == write)

    def testHash(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(hash(read), hash(self.api.Permission.Values.READ))
        self.assertEqual(hash(read), hash(self.api.Permission.fromValue(self.READ_VALUE)))
        self.assertEqual(hash(write), hash(self.api.Permission.Values.WRITE))
        self.assertEqual(hash(write), hash(self.api.Permission.fromValue(self.WRITE_VALUE)))
        self.assertNotEqual(hash(read), hash(write))
        self.assertNotEqual(hash(read), hash(self.api.Permission.Values.NONE))

    def testStr(self):
        self.assertEqual("0[NONE]", str(self.api.Permission.Values.NONE))
        self.assertEqual("2[READ]", str(self.api.Permission.Values.READ))
        self.assertEqual("4[WRITE]", str(self.api.Permission.Values.WRITE))
        self.assertEqual("6[READ | WRITE | CREATE]", (
            str(self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)))
        self.assertEqual("7[READ | WRITE | CREATE]", str(self.api.Permission.fromValue(7)))

    def testOr(self):
        read = self.api.Permission.Values.READ
        write = self.api.Permission.Values.WRITE

        self.assertEqual(read | write, self.api.Permission.Values.READ | self.api.Permission.Values.WRITE)
        self.assertEqual(read | self.api.Permission.Values.WRITE, self.api.Permission.Values.READ | write)
        self.assertEqual(read, read | self.api.Permission.Values.NONE)
        self.assertEqual(write, self.api.Permission.Values.NONE | write)

        self.assertEqual(self.READ_VALUE | self.WRITE_VALUE, (read | write).value)

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
        self.assertEqual(self.READ_VALUE ^ self.WRITE_VALUE, (read ^ write).value)
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
        self.assertEqual(PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE.bitSizeOf())
        self.assertEqual(PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE.bitSizeOf(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        self.assertEqual(bitPosition + PERMISSION_BITSIZEOF,
                         self.api.Permission.Values.READ.initializeOffsets(bitPosition))

    def testWrite(self):
        permission = self.api.Permission.Values.READ
        writer = zserio.BitStreamWriter()
        permission.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readPermission = self.api.Permission.fromReader(reader)
        self.assertEqual(permission, readPermission)

    def testGetValue(self):
        self.assertEqual(self.NONE_VALUE, self.api.Permission.Values.NONE.value)
        self.assertEqual(self.READ_VALUE, self.api.Permission.Values.READ.value)
        self.assertEqual(self.WRITE_VALUE, self.api.Permission.Values.WRITE.value)

PERMISSION_BITSIZEOF = 7
