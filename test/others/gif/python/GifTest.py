import os

import unittest
import zserio

from testutils import getZserioApi

class GifTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "gif.zs")

    def testOnePixGif(self):
        reader = zserio.BitStreamReader.fromFile(self.ONE_PIX_GIF_FILE_NAME)
        gifFile = self.api.GifFile.fromReader(reader)

        signatureFormat = gifFile.getSignature().getFormat()
        for i in range(len(self.GIF_FILE_FORMAT)):
            self.assertEqual(self.GIF_FILE_FORMAT[i], chr(signatureFormat[i]))

        signatureVersion = gifFile.getSignature().getVersion()
        for i in range(len(self.GIF_FILE_VERSION)):
            self.assertEqual(self.GIF_FILE_VERSION[i], chr(signatureVersion[i]))

        screenDescriptor = gifFile.getScreen()
        self.assertEqual(self.GIF_SCREEN_WIDTH, screenDescriptor.getWidth())
        self.assertEqual(self.GIF_SCREEN_HEIGHT, screenDescriptor.getHeight())
        self.assertEqual(self.GIF_SCREEN_BG_COLOR, screenDescriptor.getBgColor())
        self.assertEqual(self.GIF_SCREEN_BITS_OF_COLOR_RESOLUTION, screenDescriptor.getBitsOfColorResulution())
        self.assertEqual(self.GIF_SCREEN_BITS_PER_PIXEL, screenDescriptor.getBitsPerPixel())


    ONE_PIX_GIF_FILE_NAME = os.path.join(os.path.dirname(os.path.realpath(__file__)), "..", "data", "1pix.gif")

    GIF_FILE_FORMAT = "GIF"
    GIF_FILE_VERSION = "89a"
    GIF_SCREEN_WIDTH = 256
    GIF_SCREEN_HEIGHT = 256
    GIF_SCREEN_BG_COLOR = 255
    GIF_SCREEN_BITS_OF_COLOR_RESOLUTION = 7
    GIF_SCREEN_BITS_PER_PIXEL = 7
