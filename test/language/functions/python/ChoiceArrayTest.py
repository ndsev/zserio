import unittest
import zserio

from testutils import getZserioApi

class ChoiceArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").choice_array

        cls.ELEMENT_A_FOR_EXTRA_VALUE = 20
        cls.EXTRA_VALUE = 4711
        cls.ITEMS = [cls.api.Item(12, 13),
                     cls.api.Item(cls.ELEMENT_A_FOR_EXTRA_VALUE, 18),
                     cls.api.Item(17, 14)]
        cls.NUM_ITEMS = len(cls.ITEMS)
        cls.EXPLICIT_ITEM = cls.api.Item(27, 29)

    def testChoiceArrayFunctionElement0(self):
        self._checkChoiceArrayFunction(0)

    def testChoiceArrayFunctionElement1(self):
        self._checkChoiceArrayFunction(1)

    def testChoiceArrayFunctionElement2(self):
        self._checkChoiceArrayFunction(2)

    def testChoiceArrayFunctionExplicitElement(self):
        self._checkChoiceArrayFunction(self.NUM_ITEMS)

    def _writeOuterArrayToStream(self, writer, pos):
        writer.writeBits(self.NUM_ITEMS, 16)

        for item in self.ITEMS:
            writer.writeBits(item.getA(), 8)
            writer.writeBits(item.getB(), 8)

        isExplicit = 1 if pos >= self.NUM_ITEMS else 0
        writer.writeBits(isExplicit, 8)
        if isExplicit != 0:
            writer.writeBits(self.EXPLICIT_ITEM.getA(), 8)
            writer.writeBits(self.EXPLICIT_ITEM.getB(), 8)
            elementA = self.EXPLICIT_ITEM.getA()
        else:
            writer.writeBits(pos, 16)
            elementA = self.ITEMS[pos].getA()

        if elementA == self.ELEMENT_A_FOR_EXTRA_VALUE:
            writer.writeSignedBits(self.EXTRA_VALUE, 32)

    def _createInner(self, pos):
        outerArray = self.api.OuterArray()

        outerArray.setNumElements(self.NUM_ITEMS)
        outerArray.setValues(self.ITEMS)

        inner = self.api.Inner()
        inner.setOuterArray(outerArray)

        isExplicit = 1 if pos >= self.NUM_ITEMS else 0
        inner.setIsExplicit(isExplicit)

        itemRef = self.api.ItemRef(inner.getIsExplicit(), outerArray)
        if isExplicit != 0:
            itemRef.setItem(self.EXPLICIT_ITEM)
            elementA = self.EXPLICIT_ITEM.getA()
        else:
            itemRef.setPos(pos)
            elementA = self.ITEMS[pos].getA()
        inner.setRef(itemRef)

        if elementA == self.ELEMENT_A_FOR_EXTRA_VALUE:
            inner.setExtra(self.EXTRA_VALUE)

        return inner

    def _checkChoiceArrayFunction(self, pos):
        inner = self._createInner(pos)
        readElement = inner.getRef().funcGetElement()
        if pos >= self.NUM_ITEMS:
            self.assertEqual(self.EXPLICIT_ITEM, readElement)
        else:
            self.assertEqual(self.ITEMS[pos], readElement)

        writer = zserio.BitStreamWriter()
        inner.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeOuterArrayToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())
        self.assertTrue(expectedWriter.getBitPosition() == writer.getBitPosition())

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readInner = self.api.Inner.fromReader(reader)
        self.assertEqual(inner, readInner)
