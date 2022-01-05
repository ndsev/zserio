package without_pubsub_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;

public class WithoutPubsubCodeTest
{
    @Test
    public void checkService()
    {
        assertTrue(isFilePresent("../gen/without_pubsub_code/Service.java"));
    }

    @Test
    public void checkRequest()
    {
        assertTrue(isFilePresent("../gen/without_pubsub_code/Request.java"));
    }

    @Test
    public void checkResponse()
    {
        assertTrue(isFilePresent("../gen/without_pubsub_code/Response.java"));
    }

    @Test
    public void checkPubsub()
    {
        assertFalse(isFilePresent("../gen/without_pubsub_code/Pubsub.java"));
    }

    private boolean isFilePresent(String fileName)
    {
        File file = new File(fileName);
        return file.exists();
    }
};
