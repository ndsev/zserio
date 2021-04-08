import unittest
import zserio

from testutils import getZserioApi

def _convertRgbToCmyk(rgb):
    # see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html
    rr = rgb[0] / 255. * 100
    gg = rgb[1] / 255. * 100
    bb = rgb[2] / 255. * 100

    k = 100. - max(rr, gg, bb)

    c = round((100. - rr - k) / (100. - k) * 100)
    m = round((100. - gg - k) / (100. - k) * 100)
    y = round((100. - bb - k) / (100. - k) * 100)
    k = round(k)
    return (c, m, y, k)

def _convertCmykToRgb(cmyk):
    # see https://www.rapidtables.com/convert/color/cmyk-to-rgb.html
    r = round(255 * (1 - cmyk[0] / 100.) * (1 - cmyk[3] / 100.))
    g = round(255 * (1 - cmyk[1] / 100.) * (1 - cmyk[3] / 100.))
    b = round(255 * (1 - cmyk[2] / 100.) * (1 - cmyk[3] / 100.))
    return (r, g, b)

RGB_VALUES = [(0, 128, 255), (222, 222, 0), (65, 196, 31)]
CMYK_VALUES = [
    _convertRgbToCmyk(RGB_VALUES[0]),
    _convertRgbToCmyk(RGB_VALUES[1]),
    _convertRgbToCmyk(RGB_VALUES[2])
]

class LocalServiceClient(zserio.ServiceClientInterface):
    def __init__(self, service):
        self._service = service

    def call_method(self, method_name, request, context = None):
        response = self._service.call_method(method_name, request.byte_array, context)

        return response.byte_array

class ComplexTypesServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "service_types.zs").complex_types_service

        class Service(cls.api.ComplexTypesService.Service):
            def _swap_models_impl(self, request, _context):
                requestData = request.data
                data = requestData.data

                response = cls.api.Response()
                response.length = len(data)
                response.data = cls.api.ResponseData(len(data))

                if requestData.model == cls.api.ColorModel.RGB:
                    self._rgbToCmyk(data, response)
                else:
                    self._cmykToRgb(data, response)

                return response

            @staticmethod
            def _get_length_impl(request, _context):
                requestData = request.data
                lengthResponse = cls.api.LengthResponse(len(requestData.data))
                return lengthResponse

            @staticmethod
            def _rgbToCmyk(data, response):
                cmykData = []
                for element in data:
                    rgbModel = element.rgb
                    rgb = (rgbModel.red, rgbModel.green, rgbModel.blue)
                    cmyk = _convertRgbToCmyk(rgb)
                    cmykModel = cls.api.CMYKModel(cmyk[0], cmyk[1], cmyk[2], cmyk[3])
                    cmykData.append(cmykModel)
                response.data.cmyk_data = cmykData

            @staticmethod
            def _cmykToRgb(data, response):
                rgbData = []
                for element in data:
                    cmykModel = element.cmyk
                    cmyk = (cmykModel.cyan, cmykModel.magenta, cmykModel.yellow, cmykModel.key)
                    rgb = _convertCmykToRgb(cmyk)
                    rgbModel = cls.api.RGBModel(rgb[0], rgb[1], rgb[2])
                    rgbData.append(rgbModel)
                response.data.rgb_data = rgbData

        cls.Service = Service

    def setUp(self):
        self.service = self.Service()
        self.client = self.api.ComplexTypesService.Client(LocalServiceClient(self.service))

    def testServiceFullName(self):
        self.assertEqual("service_types.complex_types_service.ComplexTypesService",
                         self.service.service_full_name)

    def testMethodNames(self):
        self.assertEqual("swapModels", self.service.method_names[0])
        self.assertEqual("getLength", self.service.method_names[1])

    def testRgbToCmyk(self):
        length = 10000
        offsets = [0] * length
        data = []
        for i in range(length):
            choice = self.api.ColorModelChoice(self.api.ColorModel.RGB)
            choice.rgb = self.api.RGBModel(RGB_VALUES[i % 3][0], RGB_VALUES[i % 3][1], RGB_VALUES[i % 3][2])
            data.append(choice)

        requestData = self.api.RequestData(self.api.ColorModel.RGB, offsets, data)
        request = self.api.Request(self.api.ColorModel.RGB, requestData)

        self.assertEqual(length, self.client.get_length(request).length)

        response = self.client.swap_models(request)
        self.assertEqual(length, response.length)

        cmykData = response.data.cmyk_data
        for i, cmyk in enumerate(cmykData):
            self.assertEqual(CMYK_VALUES[i % 3][0], cmyk.cyan)
            self.assertEqual(CMYK_VALUES[i % 3][1], cmyk.magenta)
            self.assertEqual(CMYK_VALUES[i % 3][2], cmyk.yellow)
            self.assertEqual(CMYK_VALUES[i % 3][3], cmyk.key)

    def testCmykToRgb(self):
        length = 10000
        offsets = [0] * length
        data = []
        for i in range(length):
            choice = self.api.ColorModelChoice(self.api.ColorModel.CMYK)
            choice.cmyk = self.api.CMYKModel(CMYK_VALUES[i % 3][0],
                                             CMYK_VALUES[i % 3][1],
                                             CMYK_VALUES[i % 3][2],
                                             CMYK_VALUES[i % 3][3])
            data.append(choice)

        requestData = self.api.RequestData(self.api.ColorModel.CMYK, offsets, data)
        request = self.api.Request(self.api.ColorModel.CMYK, requestData)

        self.assertEqual(length, self.client.get_length(request).length)

        response = self.client.swap_models(request)
        self.assertEqual(length, response.length)

        rgbData = response.data.rgb_data
        for i, rgb in enumerate(rgbData):
            self.assertEqual(RGB_VALUES[i % 3][0], rgb.red)
            self.assertEqual(RGB_VALUES[i % 3][1], rgb.green)
            self.assertEqual(RGB_VALUES[i % 3][2], rgb.blue)

    def testInvalidServiceMethod(self):
        with self.assertRaises(zserio.ServiceException):
            self.service.call_method("nonexistentMethod", bytes())
