import unittest
import warnings

from zserio.enum import Enum, DeprecatedItem


class EnumTest(unittest.TestCase):
    class TestEnum(Enum):
        ONE = 1, DeprecatedItem
        TWO = 2

    def test_deprecation_getattribute(self):
        with warnings.catch_warnings(record=True) as caught_warnings:
            self.assertEqual(2, EnumTest.TestEnum.TWO.value)
            self.assertEqual(0, len(caught_warnings))

        with self.assertWarns(DeprecationWarning):
            self.assertEqual(1, EnumTest.TestEnum.ONE.value)

    def test_deprecation_getitem(self):
        with warnings.catch_warnings(record=True) as caught_warnings:
            self.assertEqual(2, EnumTest.TestEnum["TWO"].value)
            self.assertEqual(0, len(caught_warnings))

        with self.assertWarns(DeprecationWarning):
            self.assertEqual(1, EnumTest.TestEnum["ONE"].value)

    def test_deprecation_call(self):
        with warnings.catch_warnings(record=True) as caught_warnings:
            self.assertEqual(EnumTest.TestEnum.TWO, EnumTest.TestEnum(2))
            self.assertEqual(0, len(caught_warnings))

        with self.assertWarns(DeprecationWarning):
            self.assertEqual(EnumTest.TestEnum.ONE, EnumTest.TestEnum(1))

    def test_functional_syntax(self):
        # pylint: disable=invalid-name
        TestEnumColor = Enum("TestEnumColor", ["BLUE", "RED", "GREEN"])
        self.assertEqual(TestEnumColor.BLUE, TestEnumColor(1))
        self.assertEqual(TestEnumColor.RED, TestEnumColor(2))
        self.assertEqual(TestEnumColor.GREEN, TestEnumColor(3))

    def test_lt(self):
        class OtherEnum(Enum):
            ONE = 1
            TWO = 2

        class ReversedEnum(Enum):
            TWO = 2
            ONE = 1

        with warnings.catch_warnings():
            warnings.simplefilter("ignore", DeprecationWarning)
            one = EnumTest.TestEnum.ONE
        self.assertTrue(one < EnumTest.TestEnum.TWO)
        self.assertFalse(EnumTest.TestEnum.TWO < one)
        self.assertFalse(one < one)  # pylint: disable=comparison-with-itself

        # ordered by values, not by the declaration order
        self.assertTrue(ReversedEnum.ONE < ReversedEnum.TWO)
        self.assertEqual([ReversedEnum.ONE, ReversedEnum.TWO], sorted(ReversedEnum))

        with self.assertRaises(TypeError):
            _ = OtherEnum.ONE < EnumTest.TestEnum.TWO

        with self.assertRaises(TypeError):
            _ = OtherEnum.ONE < 2

    def test_invalid_argument(self):
        with self.assertRaises(ValueError):
            # pylint: disable=unused-variable
            class EnumInvalidDeprecatedItemObject(Enum):
                ONE = 1, DeprecatedItem()

        with self.assertRaises(ValueError):
            # pylint: disable=unused-variable
            class EnumInvalidDeprecatedItemInt(Enum):
                ONE = 1, 2

        with self.assertRaises(ValueError):
            # pylint: disable=unused-variable
            class EnumInvalidDeprecatedItemStr(Enum):
                ONE = 1, "deprecated"
