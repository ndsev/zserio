import unittest
import zserio

from testutils import getZserioApi

class UnionArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").union_array

        cls.ITEMS = [cls.api.Item(12, 13),
                     cls.api.Item(42, b_=18),
                     cls.api.Item(a_=17, b_=14)]
        cls.NUM_ITEM_ELEMENTS = len(cls.ITEMS)
        cls.EXPLICIT_ITEM = cls.api.Item(27, 29)

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
            writer.writeBits(item.a, 8)
            writer.writeBits(item.b, 8)

        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        writer.writeVarSize(0 if isExplicit != 0 else 1)
        if isExplicit != 0:
            writer.writeBits(self.EXPLICIT_ITEM.a, 8)
            writer.writeBits(self.EXPLICIT_ITEM.b, 8)
        else:
            writer.writeBits(pos, 16)

    def _createInner(self, pos):
        outerArray = self.api.OuterArray()
        outerArray.num_elements = self.NUM_ITEM_ELEMENTS
        outerArray.values = self.ITEMS

        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        itemRef = self.api.ItemRef(outerArray)
        if isExplicit != 0:
            itemRef.item = self.EXPLICIT_ITEM
        else:
            itemRef.position = pos

        return self.api.Inner(outerArray, itemRef)

    def _checkInner(self, pos):
        inner = self._createInner(pos)
        isExplicit = 1 if pos >= self.NUM_ITEM_ELEMENTS else 0
        if isExplicit != 0:
            self.assertEqual(self.EXPLICIT_ITEM, inner.ref.funcGetItem())
        else:
            self.assertEqual(self.ITEMS[pos], inner.ref.funcGetElement())

        writer = zserio.BitStreamWriter()
        inner.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeInnerToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())
        self.assertTrue(expectedWriter.getBitPosition() == writer.getBitPosition())

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readInner = self.api.Inner.fromReader(reader)

        self.assertEqual(inner, readInner)
