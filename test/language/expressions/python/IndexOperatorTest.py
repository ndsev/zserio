import unittest
import zserio

from testutils import getZserioApi

class IndexOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").index_operator

    def testZeroLength(self):
        elementList = self._createElementList(0)
        self.assertEqual(self.LENGTH_SIZE, elementList.bitsizeof())
        self.assertEqual(0, self._createReadElementList(elementList).length)

    def testOneElement(self):
        length = 1
        elementList = self._createElementList(length)
        self.assertEqual(self.LENGTH_SIZE + self.FIELD16_SIZE, elementList.bitsizeof())
        self._checkElements(self._createReadElementList(elementList), length)

    def testTwoElements(self):
        length = 2
        elementList = self._createElementList(length)
        self.assertEqual(self.LENGTH_SIZE + self.FIELD16_SIZE + self.FIELD8_SIZE, elementList.bitsizeof())
        self._checkElements(self._createReadElementList(elementList), length)

    def testThreeElements(self):
        length = 3
        elementList = self._createElementList(length)
        self.assertEqual(self.LENGTH_SIZE + self.FIELD16_SIZE + self.FIELD8_SIZE + self.FIELD16_SIZE,
                         elementList.bitsizeof())
        self._checkElements(self._createReadElementList(elementList), length)

    def testFourElements(self):
        length = 4
        elementList = self._createElementList(length)
        self.assertEqual(self.LENGTH_SIZE + self.FIELD16_SIZE + self.FIELD8_SIZE + self.FIELD16_SIZE +
                         self.FIELD8_SIZE, elementList.bitsizeof())
        self._checkElements(self._createReadElementList(elementList), length)

    def _createElementList(self, length):
        elements = []
        for i in range(length):
            isEven = i % 2 + 1 == 2
            element = self.api.Element(isEven)
            if isEven:
                element.field8 = self.ELEMENTS[i]
            else:
                element.field16 = self.ELEMENTS[i]
            elements.append(element)

        return self.api.ElementList(len(elements), elements)

    def _createReadElementList(self, elementList):
        bitBuffer = zserio.serialize(elementList)
        readElementList = zserio.deserialize(self.api.ElementList, bitBuffer)

        return readElementList

    def _checkElements(self, elementList, length):
        self.assertEqual(length, elementList.length)
        for i in range(length):
            isEven = i % 2 + 1 == 2
            element = elementList.elements[i]
            self.assertEqual(self.ELEMENTS[i], element.field8 if isEven else element.field16)

    ELEMENTS = [11, 33, 55, 77]
    LENGTH_SIZE = 16
    FIELD8_SIZE = 8
    FIELD16_SIZE = 16
