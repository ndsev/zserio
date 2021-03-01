import unittest

from zserio.hashcode import calc_hashcode, HASH_SEED

class HashCodeTest(unittest.TestCase):

    def test_calc_hashcode(self):
        array1 = [1, 2, 3, 4]
        array2 = [1, 2, 3, 3]
        array3 = [1, 2, 3, 4]
        self.assertNotEqual(self._hashcode(array1), self._hashcode(array2))
        self.assertEqual(self._hashcode(array1), self._hashcode(array3))

    @staticmethod
    def _hashcode(array):
        hashcode = HASH_SEED
        for element in array:
            hashcode = calc_hashcode(hashcode, hash(element))

        return hashcode
