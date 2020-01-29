import os
import sys
sys.path.insert(0, os.path.dirname(__file__)) # zserio_service_pb2_grpc expects zserio_service_pb2 on path
import zserio_service_grpc.zserio_service_pb2
import zserio_service_grpc.zserio_service_pb2_grpc
sys.path.pop(0)

class GrpcServicer(zserio_service_grpc.zserio_service_pb2_grpc.ZserioServiceServicer):
    """ Generic GRPC service helper library for Zserio generated servies. """

    def __init__(self, service):
        self._service = service

    def callProcedure(self, request, context):
        responseData = self._service.callProcedure(request.procName, request.requestData)
        return zserio_service_grpc.zserio_service_pb2.Response(responseData=bytes(responseData))

class GrpcClient:
    """ Used as a service from Zserio generated client! """

    def __init__(self, channel):
        self._stub = zserio_service_grpc.zserio_service_pb2_grpc.ZserioServiceStub(channel)

    def callProcedure(self, procName, requestData):
        response = self._stub.callProcedure(zserio_service_grpc.zserio_service_pb2.Request(
            procName=procName, requestData=bytes(requestData)))
        return response.responseData

def registerService(service, server):
    grpcServicer = zserio_service_grpc.zserio_service.GrpcServicer(service)
    zserio_service_grpc.zserio_service_pb2_grpc.add_ZserioServiceServicer_to_server(grpcServicer, server)