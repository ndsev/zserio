import unittest
import zserio

from testutils import getZserioApi

class StructureOptionalTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_optional

    def testDefaultValueConsumerCreator(self):
        self._checkValueConsumerCreator(self.DEFAULT_VALUE, self.EXTERNAL_VALUE)

    def testExternalValueConsumerCreator(self):
        self._checkValueConsumerCreator(self.INVALID_DEFAULT_VALUE, self.EXTERNAL_VALUE)

    def _calculateValue(self, defaultValue, externalValue):
        return defaultValue if defaultValue != self.INVALID_DEFAULT_VALUE else externalValue

    def _writeValueConsumerCreatorToStream(self, writer, defaultValue, externalValue):
        writer.write_bits(defaultValue, 4)
        if defaultValue == self.INVALID_DEFAULT_VALUE:
            writer.write_bits(externalValue, 4)
        writer.write_bool(self._calculateValue(defaultValue, externalValue) < self.SMALL_VALUE_THRESHOLD)

    def _createValueConsumerCreator(self, defaultValue, externalValue):
        valueCalculator = self.api.ValueCalculator()
        valueCalculator.default_value = defaultValue
        if defaultValue == self.INVALID_DEFAULT_VALUE:
            valueCalculator.external_value = externalValue

        valueConsumer = self.api.ValueConsumer(valueCalculator.func_value())
        valueConsumer.is_small = self._calculateValue(defaultValue, externalValue) < self.SMALL_VALUE_THRESHOLD

        return self.api.ValueConsumerCreator(valueCalculator, valueConsumer)

    def _checkValueConsumerCreator(self, defaultValue, externalValue):
        valueConsumerCreator = self._createValueConsumerCreator(defaultValue, externalValue)
        self.assertEqual(self._calculateValue(defaultValue, externalValue),
                         valueConsumerCreator.value_calculator.func_value())

        writer = zserio.BitStreamWriter()
        valueConsumerCreator.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeValueConsumerCreatorToStream(expectedWriter, defaultValue, externalValue)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readValueConsumerCreator = self.api.ValueConsumerCreator.from_reader(reader)
        self.assertEqual(valueConsumerCreator, readValueConsumerCreator)

    INVALID_DEFAULT_VALUE = 0
    DEFAULT_VALUE = 1
    EXTERNAL_VALUE = 2
    SMALL_VALUE_THRESHOLD = 8
