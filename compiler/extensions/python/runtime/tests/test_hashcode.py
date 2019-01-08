import unittest

from zserio.hashcode import calcHashCode, HASH_SEED

class HashCodeTest(unittest.TestCase):

    def testCalcHashCode(self):
        array1 = [1, 2, 3, 4]
        array2 = [1, 2, 3, 3]
        array3 = [1, 2, 3, 4]
        self.assertNotEqual(self._hashCode(array1), self._hashCode(array2))
        self.assertEqual(self._hashCode(array1), self._hashCode(array3))

    @staticmethod
    def _hashCode(array):
        hashCode = HASH_SEED
        for element in array:
            hashCode = calcHashCode(hashCode, hash(element))

        return hashCode
