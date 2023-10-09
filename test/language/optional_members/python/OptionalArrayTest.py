import OptionalMembers

class OptionalArrayTest(OptionalMembers.TestCase):
    def testData8(self):
        numElements = 4
        test = self.api.TestStruct()
        test.has_data8 = True
        test.data8 = [self.api.Data8(i) for i in range(numElements)]
        self.assertEqual(numElements, len(test.data8))

        self.assertFalse(test.is_data16_set())
        self.assertFalse(test.is_data16_used())
        numElements = 6
        test.data16 = list(range(numElements))
        self.assertTrue(test.is_data16_set())
        self.assertFalse(test.is_data16_used())

    def testAutoData8(self):
        numElements = 5
        test = self.api.TestStruct()
        self.assertFalse(test.is_auto_data8_set())
        self.assertFalse(test.is_auto_data8_used())
        test.auto_data8 = [self.api.Data8(i) for i in range(numElements)]
        self.assertTrue(test.is_auto_data8_set())
        self.assertTrue(test.is_auto_data8_used())
        self.assertEqual(numElements, len(test.auto_data8))

    def testData16(self):
        numElements = 6
        test = self.api.TestStruct()
        test.has_data8 = False
        test.data16 = list(range(numElements))
        self.assertEqual(numElements, len(test.data16))

        self.assertFalse(test.is_data8_set())
        self.assertFalse(test.is_data8_used())
        numElements = 4
        test.data8 = [self.api.Data8(i) for i in range(numElements)]
        self.assertTrue(test.is_data8_set())
        self.assertFalse(test.is_data8_used())

    def testAutoData16(self):
        numElements = 7
        test = self.api.TestStruct()
        self.assertFalse(test.is_auto_data16_set())
        self.assertFalse(test.is_auto_data16_used())
        test.auto_data16 = list(range(numElements))
        self.assertTrue(test.is_auto_data16_set())
        self.assertTrue(test.is_auto_data16_used())
        self.assertEqual(numElements, len(test.auto_data16))
