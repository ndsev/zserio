package pubsub_types.simple_pubsub;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;
import zserio.runtime.pubsub.PubsubCallback;
import zserio.runtime.pubsub.PubsubException;

import pubsub_types.TestPubsub;

public class SimplePubsubTest
{
    @BeforeEach
    public void setUp()
    {
        pubsub = new TestPubsub();
        simplePubsubProvider = new SimplePubsubProvider(pubsub);
        simplePubsubClient = new SimplePubsubClient(pubsub);
        simplePubsub = new SimplePubsub(pubsub);
    }

    @Test
    public void powerOfTwoClientAndProvider()
    {
        simplePubsubProvider.subscribeRequest(new ProviderCallback(simplePubsubProvider));

        final Map<Integer, BigInteger> results = new HashMap<Integer, BigInteger>();
        simplePubsubClient.subscribePowerOfTwo(new PowerOfTwoCallback(results, 0));

        final Int32Value request = new Int32Value(13);
        simplePubsubClient.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0));

        request.setValue(-13);
        simplePubsubClient.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0));

        request.setValue(2);
        simplePubsubClient.publishRequest(request);
        assertEquals(BigInteger.valueOf(4), results.get(0));

        request.setValue(-2);
        simplePubsubClient.publishRequest(request);
        assertEquals(BigInteger.valueOf(4), results.get(0));
    }

    @Test
    public void powerOfTwoSimplePubsub()
    {
        simplePubsub.subscribeRequest(new RequestCallback(simplePubsub));

        final Map<Integer, BigInteger> results = new HashMap<Integer, BigInteger>();
        simplePubsub.subscribePowerOfTwo(new PowerOfTwoCallback(results, 0));

        final Int32Value request = new Int32Value(13);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0));

        request.setValue(-13);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0));

        request.setValue(2);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(4), results.get(0));

        request.setValue(-2);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(4), results.get(0));
    }

    @Test
    public void powerOfTwoRawClientAndProvider()
    {
        simplePubsubProvider.subscribeRequestRaw(new ProviderRawCallback(simplePubsubProvider));

        final Map<Integer, BigInteger> results = new HashMap<Integer, BigInteger>();
        simplePubsubClient.subscribePowerOfTwoRaw(new PowerOfTwoRawCallback(results, 0));

        final Int32Value request = new Int32Value(13);
        final byte[] requestData = SerializeUtil.serializeToBytes(request);
        simplePubsubClient.publishRequestRaw(requestData);
        assertEquals(BigInteger.valueOf(169), results.get(0));
    }

    @Test
    public void powerOfTwoRawSimplePubsub()
    {
        simplePubsub.subscribeRequestRaw(new RequestRawCallback(simplePubsub));

        final Map<Integer, BigInteger> results = new HashMap<Integer, BigInteger>();
        simplePubsub.subscribePowerOfTwoRaw(new PowerOfTwoRawCallback(results, 0));

        final Int32Value request = new Int32Value(13);
        final byte[] requestData = SerializeUtil.serializeToBytes(request);
        simplePubsub.publishRequestRaw(requestData);
        assertEquals(BigInteger.valueOf(169), results.get(0));
    }

    @Test
    public void publishRequestWithContext()
    {
        final TestPubsub.TestPubsubContext context = new TestPubsub.TestPubsubContext();
        assertFalse(context.seenByPubsub);
        final Int32Value request = new Int32Value(42);
        simplePubsub.publishRequest(request, context);
        assertTrue(context.seenByPubsub);
    }

    @Test
    public void subscribeRequestWithContext()
    {
        final TestPubsub.TestPubsubContext context = new TestPubsub.TestPubsubContext();
        assertFalse(context.seenByPubsub);
        simplePubsub.subscribeRequest(new EmptyCallbackInt32Value(), context);
        assertTrue(context.seenByPubsub);
    }

    @Test
    public void unsubscribe()
    {
        int id0 = simplePubsub.subscribeRequest(new RequestCallback(simplePubsub));

        final Map<Integer, BigInteger> results = new HashMap<Integer, BigInteger>();
        int id1 = simplePubsub.subscribePowerOfTwo(new PowerOfTwoCallback(results, 0));
        int id2 = simplePubsub.subscribePowerOfTwo(new PowerOfTwoCallback(results, 1));

        final Int32Value request = new Int32Value(13);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0));
        assertEquals(BigInteger.valueOf(169), results.get(1));

        simplePubsub.unsubscribe(id1);
        request.setValue(2);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0)); // shall not be changed!
        assertEquals(BigInteger.valueOf(4), results.get(1));

        simplePubsub.unsubscribe(id0); // unsubscribe publisher
        request.setValue(3);
        simplePubsub.publishRequest(request);
        assertEquals(BigInteger.valueOf(169), results.get(0)); // shall not be changed!
        assertEquals(BigInteger.valueOf(4), results.get(1)); // shall not be changed!

        simplePubsub.unsubscribe(id2);
    }

    @Test
    public void unsubscribeInvalid()
    {
        assertThrows(PubsubException.class, () -> simplePubsub.unsubscribe(0));
    }

    private static class EmptyCallbackInt32Value implements PubsubCallback<Int32Value>
    {
        @Override
        public void invoke(String topic, Int32Value message)
        {}
    }

    private static class ProviderCallback implements PubsubCallback<Int32Value>
    {
        public ProviderCallback(SimplePubsubProvider provider)
        {
            this.provider = provider;
        }

        @Override
        public void invoke(String topic, Int32Value value)
        {
            assertEquals("simple_pubsub/request", topic);
            final UInt64Value result = new UInt64Value(BigInteger.valueOf(value.getValue()).pow(2));
            provider.publishPowerOfTwo(result);
        }

        private final SimplePubsubProvider provider;
    }

    private static class RequestCallback implements PubsubCallback<Int32Value>
    {
        public RequestCallback(SimplePubsub pubsub)
        {
            this.pubsub = pubsub;
        }

        @Override
        public void invoke(String topic, Int32Value value)
        {
            assertEquals("simple_pubsub/request", topic);
            final UInt64Value result = new UInt64Value(BigInteger.valueOf(value.getValue()).pow(2));
            pubsub.publishPowerOfTwo(result);
        }

        private final SimplePubsub pubsub;
    }

    private static class PowerOfTwoCallback implements PubsubCallback<UInt64Value>
    {
        public PowerOfTwoCallback(Map<Integer, BigInteger> results, int resultId)
        {
            this.results = results;
            this.resultId = resultId;
        }

        @Override
        public void invoke(String topic, UInt64Value value)
        {
            assertEquals("simple_pubsub/power_of_two", topic);
            results.put(resultId, value.getValue());
        }

        private final Map<Integer, BigInteger> results;
        private final int resultId;
    }

    private static class ProviderRawCallback implements PubsubCallback<byte[]>
    {
        public ProviderRawCallback(SimplePubsubProvider provider)
        {
            this.provider = provider;
        }

        @Override
        public void invoke(String topic, byte[] valueData)
        {
            assertEquals("simple_pubsub/request_raw", topic);
            final Int32Value value = SerializeUtil.deserializeFromBytes(Int32Value.class, valueData);
            final UInt64Value result = new UInt64Value(BigInteger.valueOf(value.getValue()).pow(2));
            final byte[] resultData = SerializeUtil.serializeToBytes(result);
            provider.publishPowerOfTwoRaw(resultData);
        }

        private final SimplePubsubProvider provider;
    }

    private static class RequestRawCallback implements PubsubCallback<byte[]>
    {
        public RequestRawCallback(SimplePubsub pubsub)
        {
            this.pubsub = pubsub;
        }

        @Override
        public void invoke(String topic, byte[] valueData)
        {
            assertEquals("simple_pubsub/request_raw", topic);
            final Int32Value value = SerializeUtil.deserializeFromBytes(Int32Value.class, valueData);
            final UInt64Value result = new UInt64Value(BigInteger.valueOf(value.getValue()).pow(2));
            final byte[] resultData = SerializeUtil.serializeToBytes(result);
            pubsub.publishPowerOfTwoRaw(resultData);
        }

        private final SimplePubsub pubsub;
    }

    private static class PowerOfTwoRawCallback implements PubsubCallback<byte[]>
    {
        public PowerOfTwoRawCallback(Map<Integer, BigInteger> results, int resultId)
        {
            this.results = results;
            this.resultId = resultId;
        }

        @Override
        public void invoke(String topic, byte[] valueData)
        {
            assertEquals("simple_pubsub/power_of_two_raw", topic);
            final UInt64Value value = SerializeUtil.deserializeFromBytes(UInt64Value.class, valueData);
            results.put(resultId, value.getValue());
        }

        private final Map<Integer, BigInteger> results;
        private final int resultId;
    }

    private TestPubsub pubsub = null;
    private SimplePubsubProvider simplePubsubProvider = null;
    private SimplePubsubClient simplePubsubClient = null;
    private SimplePubsub simplePubsub = null;
}
