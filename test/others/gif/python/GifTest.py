import unittest
import os
import zserio

from testutils import getZserioApi

class GifTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "gif.zs")

    def testOnePixGif(self):
        reader = zserio.BitStreamReader.from_file(self.ONE_PIX_GIF_FILE_NAME)
        gifFile = self.api.GifFile.from_reader(reader)

        signatureFormat = gifFile.signature.format
        for i in range(len(self.GIF_FILE_FORMAT)):
            self.assertEqual(self.GIF_FILE_FORMAT[i], chr(signatureFormat[i]))

        signatureVersion = gifFile.signature.version
        for i in range(len(self.GIF_FILE_VERSION)):
            self.assertEqual(self.GIF_FILE_VERSION[i], chr(signatureVersion[i]))

        screenDescriptor = gifFile.screen
        self.assertEqual(self.GIF_SCREEN_WIDTH, screenDescriptor.width)
        self.assertEqual(self.GIF_SCREEN_HEIGHT, screenDescriptor.height)
        self.assertEqual(self.GIF_SCREEN_BG_COLOR, screenDescriptor.bg_color)
        self.assertEqual(self.GIF_SCREEN_BITS_OF_COLOR_RESOLUTION, screenDescriptor.bits_of_color_resolution)
        self.assertEqual(self.GIF_SCREEN_BITS_PER_PIXEL, screenDescriptor.bits_per_pixel)

    ONE_PIX_GIF_FILE_NAME = os.path.join(os.path.dirname(os.path.realpath(__file__)), "..", "data", "1pix.gif")

    GIF_FILE_FORMAT = "GIF"
    GIF_FILE_VERSION = "89a"
    GIF_SCREEN_WIDTH = 256
    GIF_SCREEN_HEIGHT = 256
    GIF_SCREEN_BG_COLOR = 255
    GIF_SCREEN_BITS_OF_COLOR_RESOLUTION = 7
    GIF_SCREEN_BITS_PER_PIXEL = 7
