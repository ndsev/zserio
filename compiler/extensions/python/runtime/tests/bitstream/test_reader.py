import unittest

from zserio import BitStreamReader, PythonRuntimeException

class BitStreamReaderTest(unittest.TestCase):
    def testReadBits(self):
        data = [0, 1, 255, 128, 127]
        reader = BitStreamReader(bytes(data))
        for byte in data:
            self.assertEquals(byte, reader.readBits(8))

        self.assertEquals(0, reader.readBits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(1) # no more bits available

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(-1)

    def testReadSignedBits(self):
        data = [0, 0xff, 1, 127, 0x80]
        reader = BitStreamReader(bytes(data))
        self.assertEquals(0, reader.readSignedBits(8))
        self.assertEquals(-1, reader.readSignedBits(8)) # 0xff == -1
        self.assertEquals(1, reader.readSignedBits(8))
        self.assertEquals(127, reader.readSignedBits(8))
        self.assertEquals(-128, reader.readSignedBits(8)) # 0x80 == -128

        self.assertEquals(0, reader.readSignedBits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.readSignedBits(1) # no more bits available

        with self.assertRaises(PythonRuntimeException):
            reader.readSignedBits(-1)
