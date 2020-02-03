import unittest
from concurrent import futures
import grpc

from testutils import getZserioApi

class SimpleServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "service_types.zs").simple_service

        class Client:
            def __init__(self, channel):
                self._stub = cls.api.SimpleService.SimpleServiceStub(channel)

            def powerOfTwo(self, value):
                request = cls.api.Request.fromFields(value)
                response = self._stub.powerOfTwo(request)
                return response.getValue()

            def powerOfTwoAsync(self, value):
                request = cls.api.Request.fromFields(value)
                response_future = self._stub.powerOfTwo.future(request)
                response = response_future.result()
                return response.getValue()

        class Service(cls.api.SimpleService.SimpleServiceServicer):
            @staticmethod
            def powerOfTwo(request, _context):
                value = request.getValue()
                response = cls.api.Response.fromFields(value**2)
                return response

        cls.Client = Client
        cls.Service = Service

    def setUp(self):
        self.server = grpc.server(futures.ThreadPoolExecutor())
        self.api.SimpleService.add_SimpleServiceServicer_to_server(self.Service(), self.server)
        port = self.server.add_insecure_port("localhost:0") # 0 to choose port automatically
        self.server.start()
        self.client = self.Client(grpc.insecure_channel("localhost:%d" % port))

    def tearDown(self):
        self.server.stop(0)
        self.server = None
        self.client = None

    def testServiceName(self):
        self.assertEqual("service_types.simple_service.SimpleService",
                         self.server._state.generic_handlers[0].service_name())

    def testRpcMethodName(self):
        self.assertEqual("/service_types.simple_service.SimpleService/powerOfTwo",
                         list(self.server._state.generic_handlers[0]._method_handlers)[0])

    def testPowerOfTwo(self):
        self.assertEqual(169, self.client.powerOfTwo(13))
        self.assertEqual(169, self.client.powerOfTwo(-13))
        self.assertEqual(4, self.client.powerOfTwo(2))
        self.assertEqual(4, self.client.powerOfTwo(-2))

    def testPowerOfTwoAsync(self):
        self.assertEqual(169, self.client.powerOfTwoAsync(13))
        self.assertEqual(169, self.client.powerOfTwoAsync(-13))
        self.assertEqual(4, self.client.powerOfTwoAsync(2))
        self.assertEqual(4, self.client.powerOfTwoAsync(-2))
