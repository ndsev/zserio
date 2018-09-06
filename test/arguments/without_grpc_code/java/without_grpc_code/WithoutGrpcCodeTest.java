package without_grpc_code;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class WithoutGrpcCodeTest
{
    @Test
    public void checkService()
    {
        assertFalse(isFilePresent("../gen/without_grpc_code/ServiceGrpc.java"));
        // Grpc suffix is artificially added, rather test that neither of these two variants exists
        assertFalse(isFilePresent("../gen/without_grpc_code/Service.java"));
    }

    @Test
    public void checkRequest()
    {
        assertTrue(isFilePresent("../gen/without_grpc_code/Request.java"));
    }

    @Test
    public void checkResponse()
    {
        assertTrue(isFilePresent("../gen/without_grpc_code/Response.java"));
    }

    private boolean isFilePresent(String fileName)
    {
        File file = new File(fileName);
        return file.exists();
    }
};
