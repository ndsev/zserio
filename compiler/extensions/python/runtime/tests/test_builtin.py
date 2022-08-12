import unittest

from zserio.builtin import isset, numbits

class BuiltinOperatorsTest(unittest.TestCase):

    def test_isset(self):
        class DummyBitmask:
            def __init__(self):
                self._value = 0

            @classmethod
            def from_value(cls: 'DummyBitmask', value: int) -> 'DummyBitmask':
                instance = cls()
                instance._value = value
                return instance

            class Values:
                READ: 'DummyBitmask' = None
                WRITE: 'DummyBitmask' = None
                CREATE: 'DummyBitmask' = None

            def __eq__(self, other: object) -> bool:
                return self._value == other._value

            def __and__(self, other: object) -> object:
                return DummyBitmask.from_value(self._value & other._value)

            def __or__(self, other: object) -> object:
                return DummyBitmask.from_value(self._value | other._value)

        DummyBitmask.Values.READ = DummyBitmask.from_value(1)
        DummyBitmask.Values.WRITE = DummyBitmask.from_value(2)
        DummyBitmask.Values.CREATE = DummyBitmask.from_value(1 | 2)

        self.assertTrue(isset(DummyBitmask.Values.READ, DummyBitmask.Values.READ))
        self.assertTrue(isset(DummyBitmask.Values.CREATE, DummyBitmask.Values.READ))
        self.assertTrue(isset(DummyBitmask.Values.CREATE, DummyBitmask.Values.WRITE))
        self.assertTrue(isset(DummyBitmask.Values.CREATE, DummyBitmask.Values.CREATE))
        self.assertTrue(isset(DummyBitmask.Values.CREATE, DummyBitmask.Values.READ | DummyBitmask.Values.WRITE))
        self.assertFalse(isset(DummyBitmask.Values.READ, DummyBitmask.Values.WRITE))
        self.assertFalse(isset(DummyBitmask.Values.READ, DummyBitmask.Values.CREATE))

    def test_numbits(self):
        self.assertEqual(0, numbits(0))
        self.assertEqual(1, numbits(1))
        self.assertEqual(1, numbits(2))
        self.assertEqual(2, numbits(3))
        self.assertEqual(2, numbits(4))
        self.assertEqual(3, numbits(5))
        self.assertEqual(3, numbits(6))
        self.assertEqual(3, numbits(7))
        self.assertEqual(3, numbits(8))
        self.assertEqual(4, numbits(16))
        self.assertEqual(5, numbits(32))
        self.assertEqual(6, numbits(64))
        self.assertEqual(7, numbits(128))
        self.assertEqual(8, numbits(256))
        self.assertEqual(9, numbits(512))
        self.assertEqual(10, numbits(1024))
        self.assertEqual(11, numbits(2048))
        self.assertEqual(12, numbits(4096))
        self.assertEqual(13, numbits(8192))
        self.assertEqual(14, numbits(16384))
        self.assertEqual(15, numbits(32768))
        self.assertEqual(16, numbits(65536))
        self.assertEqual(24, numbits(1 << 24))
        self.assertEqual(25, numbits((1 << 24) + 1))
        self.assertEqual(32, numbits(1 << 32))
        self.assertEqual(33, numbits((1 << 32) + 1))
        self.assertEqual(63, numbits(1 << 63))
        self.assertEqual(64, numbits((1 << 63) + 1))
