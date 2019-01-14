import unittest
from concurrent import futures
import grpc

from testutils import getZserioApi

class StreamingServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "service_types.zs").streaming_service

        class Client:
            def __init__(self, channel):
                self._stub = cls.api.UserDB.UserDBStub(channel)

            def addUser(self, name, age):
                user = cls.api.User.fromFields(name, age)
                num = self._stub.addUser(user)
                return num.getNum()

            def addUsers(self, users):
                def makeUsersIterator(users):
                    for userDict in users:
                        user = cls.api.User.fromFields(userDict["name"], userDict["age"])
                        yield user

                usersIterator = makeUsersIterator(users)
                num = self._stub.addUsers(usersIterator)
                return num.getNum()

            def getUsers(self):
                users = {}
                usersIterator = self._stub.getUsers(cls.api.Empty())
                for user in usersIterator:
                    users[user.getName()] = user.getAge()
                return users

            def getAges(self, usersQuery):
                def makeNamesIterator(usersQuery):
                    for userDict in usersQuery:
                        name = cls.api.Name.fromFields(userDict["name"])
                        yield name

                namesIterator = makeNamesIterator(usersQuery)
                agesIterator = self._stub.getAges(namesIterator)
                for index, age in enumerate(agesIterator):
                    usersQuery[index]["age"] = age.getAge()

        class Service(cls.api.UserDB.UserDBServicer):
            def __init__(self):
                self._users = {}

            def addUser(self, user, _context):
                self._users[user.getName()] = user
                num = cls.api.Num.fromFields(len(self._users))
                return num

            def addUsers(self, usersIterator, _context):
                for user in usersIterator:
                    self._users[user.getName()] = user
                num = cls.api.Num.fromFields(len(self._users))
                return num

            def getUsers(self, _empty, _context):
                for user in self._users.values():
                    yield user

            def getAges(self, nameIterator, _context):
                for name in nameIterator:
                    age = cls.api.Age.fromFields(self._users[name.getName()].getAge())
                    yield age

        cls.Client = Client
        cls.Service = Service

    def setUp(self):
        self.server = grpc.server(futures.ThreadPoolExecutor())
        self.api.UserDB.add_UserDBServicer_to_server(self.Service(), self.server)
        port = self.server.add_insecure_port("localhost:0") # 0 to choose port automatically
        self.server.start()
        self.client = self.Client(grpc.insecure_channel("localhost:%d" % port))

    def tearDown(self):
        self.server.stop(0)
        self.server = None
        self.client = None

    def testUserDatabase(self):
        # no streaming
        self.assertEqual(1, self.client.addUser("A", 10))

        # client streaming
        usersToAdd = []
        usersToAdd.append({"name": "B", "age": 15})
        usersToAdd.append({"name": "C", "age": 20})
        self.assertEqual(3, self.client.addUsers(usersToAdd))

        # no streaming
        self.assertEqual(4, self.client.addUser("D", 25))

        # server streaming
        allUsers = self.client.getUsers()
        self.assertEqual(4, len(allUsers))

        # bidi streaming
        agesQuery = []
        agesQuery.append({"name": "C"})
        agesQuery.append({"name": "B"})
        self.client.getAges(agesQuery)
        self.assertEqual(20, agesQuery[0]["age"]) # C age
        self.assertEqual(15, agesQuery[1]["age"]) # B age
