import unittest
import zserio

from testutils import getZserioApi

class UnionArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").union_array

        cls.ITEMS = [cls.api.Item.fromFields(12, 13),
                     cls.api.Item.fromFields(42, 18),
                     cls.api.Item.fromFields(17, 14)]
        cls.NUM_ITEM_ELEMENTS = len(cls.ITEMS)
        cls.EXPLICIT_ITEM = cls.api.Item.fromFields(27, 29)

    def testInnerElement0(self):
        self._checkInner(0)

    def testInnerElement1(self):
        self._checkInner(1)

    def testInnerElement2(self):
        self._checkInner(2)

    def testOuterArrayExplicitElement(self):
        self._checkInner(self.NUM_ITEM_ELEMENTS)

    def _writeInnerToStream(self, writer, pos):
        writer.writeBits(self.NUM_ITEM_ELEMENTS, 16)

        for item in self.ITEMS:
            writer.writeBits(item.getA(), 8)
            writer.writeBits(item.getB(), 8)

        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        writer.writeVarUInt64(0 if isExplicit != 0 else 1)
        if isExplicit != 0:
            writer.writeBits(self.EXPLICIT_ITEM.getA(), 8)
            writer.writeBits(self.EXPLICIT_ITEM.getB(), 8)
        else:
            writer.writeBits(pos, 16)

    def _createInner(self, pos):
        outerArray = self.api.OuterArray()
        outerArray.setNumElements(self.NUM_ITEM_ELEMENTS)
        outerArray.setValues(self.ITEMS)

        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        itemRef = self.api.ItemRef(outerArray)
        if isExplicit != 0:
            itemRef.setItem(self.EXPLICIT_ITEM)
        else:
            itemRef.setPosition(pos)

        return self.api.Inner.fromFields(outerArray, itemRef)

    def _checkInner(self, pos):
        inner = self._createInner(pos)
        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        if isExplicit != 0:
            self.assertEqual(self.EXPLICIT_ITEM, inner.getRef().funcGetItem())
        else:
            self.assertEqual(self.ITEMS[pos], inner.getRef().funcGetElement())

        writer = zserio.BitStreamWriter()
        inner.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeInnerToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())

        reader = zserio.BitStreamReader(writer.getByteArray())
        readInner = self.api.Inner.fromReader(reader)

        self.assertEqual(inner, readInner)
