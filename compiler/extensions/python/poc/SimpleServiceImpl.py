import service_poc.api as api

class SimpleServiceImpl(api.SimpleService.Service):
    """ Implementation of zserio SimpleService """

    def _powerOfTwoImpl(self, request):
        return api.Response.fromFields(request.getValue() ** 2)

    def _powerOfFourImpl(self, request):
        return api.Response.fromFields(request.getValue() ** 4)
