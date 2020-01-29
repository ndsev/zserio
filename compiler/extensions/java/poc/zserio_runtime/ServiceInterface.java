package zserio_runtime; // TODO: move to zserio runtime!

public interface ServiceInterface
{
    public byte[] callProcedure(String procName, byte[] requestData);
};
