package zserio_runtime;

public interface PubSubCallback<T>
{
    void invoke(String topic, T message);
}
