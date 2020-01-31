import http.client

class HttpClient:
    def __init__(self, url, port):
        self.connection = http.client.HTTPConnection(url, port)

    def callProcedure(self, procName, requestData):
        self.connection.request("POST", '/' + procName.replace('.', '/'), requestData)
        response = self.connection.getresponse()
        # TODO: check response status!
        responseData = response.read()
        return responseData
