package service_types.streaming_service;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

public class StreamingServiceTest
{
    @BeforeClass
    public static void init() throws IOException
    {
        final String serviceName = "UserDB";
        server = new UserDBServer(serviceName);
        server.start();
        client = new UserDBClient(serviceName);
    }

    @AfterClass
    public static void shutdown()
    {
        server.shutdown();
    }

    @Test
    public void userDB() throws InterruptedException
    {
        // no streaming
        assertEquals(1, client.addUser("A", 10));

        // client streaming
        List<User> usersToAdd = new ArrayList<User>();
        usersToAdd.add(new User("B", (short) 15));
        usersToAdd.add(new User("C", (short) 20));
        assertEquals(3, client.addUsers(usersToAdd));

        // no streaming
        assertEquals(4, client.addUser("D", 25));

        // server streaming
        List<User> allUsers = client.getUsers();
        assertEquals(4, allUsers.size());

        // bidi stremaing
        List<User> agesQuery = new ArrayList<User>();
        agesQuery.add(new User("C", (short) 0));
        agesQuery.add(new User("B", (short) 0));
        client.getAges(agesQuery);
        assertEquals((short) 20, agesQuery.get(0).getAge()); // C age
        assertEquals((short) 15, agesQuery.get(1).getAge()); // B age
    }

    private static class UserDBClient
    {
        public UserDBClient(String name)
        {
            channel = InProcessChannelBuilder.forName(name).build();
            blockingStub = UserDBGrpc.newBlockingStub(channel);
            asyncStub = UserDBGrpc.newStub(channel);
        }

        int addUser(String name, int age)
        {
            User user = new User(name, (short) age);
            try
            {
                Num num = blockingStub.addUser(user);
                return (int) num.getNum();
            }
            catch (StatusRuntimeException e)
            {
                throw new RuntimeException("Client.addUser failed with: " + e);
            }
        };

        int addUsers(List<User> users) throws InterruptedException
        {
            final int[] ret = new int[1];
            final CountDownLatch finishLatch = new CountDownLatch(1);
            io.grpc.stub.StreamObserver<Num> numObserver = new io.grpc.stub.StreamObserver<Num>()
            {
                @Override
                public void onCompleted()
                {
                    finishLatch.countDown();
                }
                @Override
                public void onError(Throwable t)
                {
                    throw new RuntimeException("Client.addUsers failed with: " + t);
                }
                @Override
                public void onNext(Num num)
                {
                    ret[0] = (int) num.getNum();
                }
            };

            io.grpc.stub.StreamObserver<User> userObserver = asyncStub.addUsers(numObserver);
            for (User user : users)
            {
                userObserver.onNext(user);
            }
            userObserver.onCompleted();

            assertTrue(finishLatch.await(5, TimeUnit.SECONDS));

            return ret[0];
        }

        List<User> getUsers()
        {
            Iterator<User> users = blockingStub.getUsers(new Empty());
            List<User> usersList = new ArrayList<User>();
            while (users.hasNext())
                usersList.add(users.next());
            return usersList;
        }

        void getAges(final List<User> users) throws InterruptedException
        {
            final CountDownLatch finishLatch = new CountDownLatch(1);
            io.grpc.stub.StreamObserver<Age> ageObserver = new io.grpc.stub.StreamObserver<Age>()
            {
                @Override
                public void onCompleted()
                {
                    finishLatch.countDown();
                }
                @Override
                public void onError(Throwable t)
                {
                    throw new RuntimeException("Client.getAges failed with: " + t);
                }
                @Override
                public void onNext(Age age)
                {
                    users.get(idx).setAge(age.getAge());
                    idx++;
                }

                private int idx = 0;
            };

            io.grpc.stub.StreamObserver<Name> nameObserver = asyncStub.getAges(ageObserver);
            for (User user : users)
                nameObserver.onNext(new Name(user.getName()));
            nameObserver.onCompleted();

            assertTrue(finishLatch.await(5, TimeUnit.SECONDS));
        }

        private final ManagedChannel channel;
        private final UserDBGrpc.UserDBBlockingStub blockingStub;
        private final UserDBGrpc.UserDBStub asyncStub;
    }

    private static class UserDBService extends UserDBGrpc.UserDBImplBase
    {
        @Override
        public void addUser(service_types.streaming_service.User request,
                io.grpc.stub.StreamObserver<service_types.streaming_service.Num> responseObserver)
        {
            users.put(request.getName(), request);
            responseObserver.onNext(new Num(users.size()));
            responseObserver.onCompleted();
        }

        @Override
        public io.grpc.stub.StreamObserver<service_types.streaming_service.User> addUsers(
                final io.grpc.stub.StreamObserver<service_types.streaming_service.Num> responseObserver)
        {
            return new io.grpc.stub.StreamObserver<User>()
            {
                @Override
                public void onCompleted()
                {
                    responseObserver.onNext(new Num(users.size()));
                    responseObserver.onCompleted();
                }
                @Override
                public void onError(Throwable t)
                {
                    throw new RuntimeException("Service.addUsers failed with: " + t);
                }
                @Override
                public void onNext(User user)
                {
                    users.put(user.getName(), user);
                }
            };
        }

        @Override
        public void getUsers(service_types.streaming_service.Empty request,
                io.grpc.stub.StreamObserver<service_types.streaming_service.User> responseObserver)
        {
            for (User user : users.values())
            {
                responseObserver.onNext(user);
            }
            responseObserver.onCompleted();
        }

        @Override
        public io.grpc.stub.StreamObserver<service_types.streaming_service.Name> getAges(
                final io.grpc.stub.StreamObserver<service_types.streaming_service.Age> responseObserver)
        {
            return new io.grpc.stub.StreamObserver<Name>()
            {
                @Override
                public void onCompleted()
                {
                    responseObserver.onCompleted();
                }
                @Override
                public void onError(Throwable t)
                {
                    throw new RuntimeException("Service.getAges failed with: " + t);
                }
                @Override
                public void onNext(Name name)
                {
                    responseObserver.onNext(new Age(users.get(name.getName()).getAge()));
                }
            };
        }

        Map<String, User> users = new HashMap<String, User>();
    }

    private static class UserDBServer
    {
        public UserDBServer(String name)
        {
            server = InProcessServerBuilder.forName(name).addService(new UserDBService()).build();
        }

        public void start() throws IOException
        {
            server.start();
        }

        public void shutdown()
        {
            server.shutdown();
        }

        private final Server server;
    }

    private static UserDBServer server;
    private static UserDBClient client;
}
