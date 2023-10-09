import zserio

import BitmaskTypes

class UInt64BitmaskTest(BitmaskTypes.TestCase):
    def testEmptyConstructor(self):
        permission = self.api.Permission()
        self.assertEqual(0, permission.value)

    def testFromValue(self):
        permission = self.api.Permission.from_value(WRITE_PERMISSION_VALUE)
        self.assertTrue((permission & self.api.Permission.Values.WRITE_PERMISSION) ==
                        self.api.Permission.Values.WRITE_PERMISSION)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(-1)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Permission.from_value(1 << PERMISSION_BITSIZEOF)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(WRITE_PERMISSION_VALUE, PERMISSION_BITSIZEOF)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        permission = self.api.Permission.from_reader(reader)
        self.assertEqual(self.api.Permission.Values.WRITE_PERMISSION, permission)

    def testEq(self):
        self.assertTrue(self.api.Permission.Values.READ_PERMISSION ==
                        self.api.Permission.Values.READ_PERMISSION)
        self.assertFalse(self.api.Permission.Values.READ_PERMISSION ==
                         self.api.Permission.Values.WRITE_PERMISSION)
        self.assertTrue(self.api.Permission.Values.WRITE_PERMISSION ==
                        self.api.Permission.Values.WRITE_PERMISSION)

        read = self.api.Permission.Values.READ_PERMISSION
        self.assertTrue(read == self.api.Permission.Values.READ_PERMISSION)
        self.assertTrue(self.api.Permission.Values.READ_PERMISSION == read)
        self.assertFalse(read == self.api.Permission.Values.WRITE_PERMISSION)
        self.assertFalse(self.api.Permission.Values.WRITE_PERMISSION == read)

        write = self.api.Permission.Values.WRITE_PERMISSION
        self.assertTrue(write == self.api.Permission.Values.WRITE_PERMISSION)
        self.assertTrue(self.api.Permission.Values.WRITE_PERMISSION == write)
        self.assertFalse(write == self.api.Permission.Values.READ_PERMISSION)
        self.assertFalse(self.api.Permission.Values.READ_PERMISSION == write)

        self.assertTrue(read == self.api.Permission.from_value(read.value)) # copy
        self.assertTrue(write == self.api.Permission.from_value(write.value)) # copy

        self.assertFalse(read == write)

    def testHash(self):
        read = self.api.Permission.Values.READ_PERMISSION
        write = self.api.Permission.Values.WRITE_PERMISSION

        self.assertEqual(hash(read), hash(self.api.Permission.Values.READ_PERMISSION))
        self.assertEqual(hash(read), hash(self.api.Permission.from_value(READ_PERMISSION_VALUE)))
        self.assertEqual(hash(write), hash(self.api.Permission.Values.WRITE_PERMISSION))
        self.assertEqual(hash(write), hash(self.api.Permission.from_value(WRITE_PERMISSION_VALUE)))
        self.assertNotEqual(hash(read), hash(write))
        self.assertNotEqual(hash(read), hash(self.api.Permission.Values.NONE_PERMISSION))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(851, self.api.Permission.Values.NONE_PERMISSION.__hash__())
        self.assertEqual(853, self.api.Permission.Values.READ_PERMISSION.__hash__())
        self.assertEqual(855, self.api.Permission.Values.WRITE_PERMISSION.__hash__())
        self.assertEqual(859, self.api.Permission.Values.CREATE_PERMISSION.__hash__())

    def testStr(self):
        self.assertEqual("0[NONE_PERMISSION]", str(self.api.Permission.Values.NONE_PERMISSION))
        self.assertEqual("2[READ_PERMISSION]", str(self.api.Permission.Values.READ_PERMISSION))
        self.assertEqual("4[WRITE_PERMISSION]", str(self.api.Permission.Values.WRITE_PERMISSION))
        self.assertEqual("6[READ_PERMISSION | WRITE_PERMISSION]", str(
            self.api.Permission.Values.READ_PERMISSION | self.api.Permission.Values.WRITE_PERMISSION))
        self.assertEqual("7[READ_PERMISSION | WRITE_PERMISSION]", str(self.api.Permission.from_value(7)))
        self.assertEqual("255[READ_PERMISSION | WRITE_PERMISSION | CREATE_PERMISSION]",
                         str(self.api.Permission.from_value(255)))

    def testOr(self):
        read = self.api.Permission.Values.READ_PERMISSION
        write = self.api.Permission.Values.WRITE_PERMISSION

        self.assertEqual(read | write, self.api.Permission.Values.READ_PERMISSION |
                         self.api.Permission.Values.WRITE_PERMISSION)
        self.assertEqual(read | self.api.Permission.Values.WRITE_PERMISSION,
                         self.api.Permission.Values.READ_PERMISSION | write)
        self.assertEqual(read, read | self.api.Permission.Values.NONE_PERMISSION)
        self.assertEqual(write, self.api.Permission.Values.NONE_PERMISSION | write)

        self.assertEqual(READ_PERMISSION_VALUE | WRITE_PERMISSION_VALUE, (read | write).value)

    def testAnd(self):
        read = self.api.Permission.Values.READ_PERMISSION
        write = self.api.Permission.Values.WRITE_PERMISSION
        readwrite = self.api.Permission.Values.READ_PERMISSION | self.api.Permission.Values.WRITE_PERMISSION

        self.assertEqual(read, readwrite & read)
        self.assertEqual(write, readwrite & write)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, readwrite &
                         self.api.Permission.Values.NONE_PERMISSION)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, read &
                         self.api.Permission.Values.NONE_PERMISSION)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, write &
                         self.api.Permission.Values.NONE_PERMISSION)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, read & write)
        self.assertEqual(read, read & read & read & read & read)

    def testXor(self):
        read = self.api.Permission.Values.READ_PERMISSION
        write = self.api.Permission.Values.WRITE_PERMISSION

        self.assertEqual(read ^ write, self.api.Permission.Values.READ_PERMISSION ^
                         self.api.Permission.Values.WRITE_PERMISSION)
        self.assertEqual(READ_PERMISSION_VALUE ^ WRITE_PERMISSION_VALUE, (read ^ write).value)
        self.assertEqual(read, (read ^ write) & read)
        self.assertEqual(write, (read ^ write) & write)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, read ^ read)
        self.assertEqual(self.api.Permission.Values.NONE_PERMISSION, write ^ write)

    def testInvert(self): # bitwise not operator
        none = self.api.Permission.Values.NONE_PERMISSION
        read = self.api.Permission.Values.READ_PERMISSION
        write = self.api.Permission.Values.WRITE_PERMISSION

        self.assertEqual(write, ~read & write)
        self.assertEqual(none, ~read & read)
        self.assertEqual(write, ~none & write)
        self.assertEqual(read, ~none & read)
        self.assertEqual(read | write, ~none & (read | write))

    def testBitSizeOf(self):
        self.assertEqual(PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE_PERMISSION.bitsizeof())
        self.assertEqual(PERMISSION_BITSIZEOF, self.api.Permission.Values.NONE_PERMISSION.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        self.assertEqual(bitPosition + PERMISSION_BITSIZEOF,
                         self.api.Permission.Values.READ_PERMISSION.initialize_offsets(bitPosition))

    def testWrite(self):
        permission = self.api.Permission.Values.READ_PERMISSION
        writer = zserio.BitStreamWriter()
        permission.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readPermission = self.api.Permission.from_reader(reader)
        self.assertEqual(permission, readPermission)

    def testGetValue(self):
        self.assertEqual(NONE_PERMISSION_VALUE, self.api.Permission.Values.NONE_PERMISSION.value)
        self.assertEqual(READ_PERMISSION_VALUE, self.api.Permission.Values.READ_PERMISSION.value)
        self.assertEqual(WRITE_PERMISSION_VALUE, self.api.Permission.Values.WRITE_PERMISSION.value)

PERMISSION_BITSIZEOF = 64

NONE_PERMISSION_VALUE = 0
READ_PERMISSION_VALUE = 2
WRITE_PERMISSION_VALUE = 4
