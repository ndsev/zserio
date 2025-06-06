import zserio
from compoundutils import comparisonOperatorsTest, writeReadTest, hashTest

import ExtendedMembers


class ExtendedPackedArrayTest(ExtendedMembers.TestCase):
    ARRAY_SIZE = 1
    PACKED_ARRAY_SIZE = 5

    ORIGINAL_BIT_SIZE = 8 + 32  # array of Elements of length 1
    EXTENDED1_BIT_SIZE = (
        zserio.bitposition.alignto(8, ORIGINAL_BIT_SIZE)  # align to 8 due to extended
        + 8  # varsize
        + 1  # is packed
        + 6  # max bit number
        + 32  # first element
    )
    EXTENDED2_BIT_SIZE = (
        zserio.bitposition.alignto(8, EXTENDED1_BIT_SIZE)  # align to 8 due to extended
        + 1  # auto optional not present
    )

    def createExtended1(self):
        data = self.api.Extended1()
        for _ in range(0, self.ARRAY_SIZE):
            data.array.append(self.api.Element())
        for _ in range(0, self.PACKED_ARRAY_SIZE):
            data.packed_array.append(self.api.Element())
        return data

    def createExtended2(self):
        data = self.api.Extended2()
        for _ in range(0, self.ARRAY_SIZE):
            data.array.append(self.api.Element())
        for _ in range(0, self.PACKED_ARRAY_SIZE):
            data.packed_array.append(self.api.Element())
        return data

    def testdefaultConstructor(self):
        data = self.api.Extended2()

        # always present when not read from stream
        self.assertEqual(True, data.is_packed_array_present())
        self.assertEqual(True, data.is_optional_packed_array_present())

        # default initialized
        self.assertEqual(0, len(data.packed_array))
        self.assertEqual(False, data.is_optional_packed_array_set())

    def testfieldConstructor(self):
        elems = []
        elems.append(self.api.Element(42))
        data = self.api.Extended2([], elems, None)

        self.assertEqual(True, data.is_packed_array_present())
        self.assertEqual(42, data.packed_array[0].value)

    def testoperatorEquality(self):
        data = self.createExtended2()
        equalData = self.createExtended2()
        # lessThanData = self.createExtended2()
        # lessThanData.packed_array[-1].value = 12

        comparisonOperatorsTest(data, equalData)

    def testbitSizeOfExtended1(self):
        data = self.createExtended1()

        self.assertEqual(self.EXTENDED1_BIT_SIZE, data.bitsizeof())

    def testbitSizeOfExtended2(self):
        data = self.createExtended2()

        self.assertEqual(self.EXTENDED2_BIT_SIZE, data.bitsizeof())

    def testwriteReadExtended2(self):
        data = self.createExtended2()

        writeReadTest(self.api.Extended2, data)

    def testwriteExtended1ReadExtended2(self):
        dataExtended1 = self.createExtended1()

        bitBuffer = zserio.serialize(dataExtended1)
        readDataExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self.assertEqual(False, readDataExtended2.is_optional_packed_array_present())

        # bit size as extended1
        self.assertEqual(self.EXTENDED1_BIT_SIZE, readDataExtended2.bitsizeof())

        # write as extened1
        bitBuffer = zserio.serialize(readDataExtended2)
        self.assertEqual(self.EXTENDED1_BIT_SIZE, bitBuffer.bitsize)

        # read extended1 again
        readDataExtended1 = zserio.deserialize(self.api.Extended1, bitBuffer)
        self.assertEqual(dataExtended1, readDataExtended1)

        # make the extended value present
        readDataExtended2.reset_optional_packed_array()
        self.assertEqual(True, readDataExtended2.is_optional_packed_array_present())
        self.assertEqual(False, readDataExtended2.is_optional_packed_array_set())  # optional not present

        # bit size as extended2
        self.assertEqual(self.EXTENDED2_BIT_SIZE, readDataExtended2.bitsizeof())

        # write as extended2
        bitBuffer = zserio.serialize(readDataExtended2)
        self.assertEqual(self.EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

        writeReadTest(self.api.Extended2, readDataExtended2)

    def teststdHash(self):
        data = self.createExtended2()
        dataHash = 3259260897
        equalData = self.createExtended2()
        diffData = self.createExtended2()
        diffData.packed_array[-1].value = 12
        diffDataHash = 3259260896

        hashTest(data, dataHash, equalData, diffData, diffDataHash)
