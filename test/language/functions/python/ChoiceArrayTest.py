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
        writer.write_bits(self.NUM_ITEMS, 16)

        for item in self.ITEMS:
            writer.write_bits(item.a, 8)
            writer.write_bits(item.b, 8)

        isExplicit = 1 if pos >= self.NUM_ITEMS else 0
        writer.write_bits(isExplicit, 8)
        if isExplicit != 0:
            writer.write_bits(self.EXPLICIT_ITEM.a, 8)
            writer.write_bits(self.EXPLICIT_ITEM.b, 8)
            elementA = self.EXPLICIT_ITEM.a
        else:
            writer.write_bits(pos, 16)
            elementA = self.ITEMS[pos].a

        if elementA == self.ELEMENT_A_FOR_EXTRA_VALUE:
            writer.write_signed_bits(self.EXTRA_VALUE, 32)

    def _createInner(self, pos):
        outerArray = self.api.OuterArray()

        outerArray.num_elements = self.NUM_ITEMS
        outerArray.values = self.ITEMS

        inner = self.api.Inner()
        inner.outer_array = outerArray

        isExplicit = 1 if pos >= self.NUM_ITEMS else 0
        inner.is_explicit = isExplicit

        itemRef = self.api.ItemRef(inner.is_explicit, outerArray)
        if isExplicit != 0:
            itemRef.item = self.EXPLICIT_ITEM
            elementA = self.EXPLICIT_ITEM.a
        else:
            itemRef.pos = pos
            elementA = self.ITEMS[pos].a
        inner.ref = itemRef

        if elementA == self.ELEMENT_A_FOR_EXTRA_VALUE:
            inner.extra = self.EXTRA_VALUE

        return inner

    def _checkChoiceArrayFunction(self, pos):
        inner = self._createInner(pos)
        readElement = inner.ref.funcGetElement()
        if pos >= self.NUM_ITEMS:
            self.assertEqual(self.EXPLICIT_ITEM, readElement)
        else:
            self.assertEqual(self.ITEMS[pos], readElement)

        writer = zserio.BitStreamWriter()
        inner.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeOuterArrayToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInner = self.api.Inner.fromReader(reader)
        self.assertEqual(inner, readInner)
