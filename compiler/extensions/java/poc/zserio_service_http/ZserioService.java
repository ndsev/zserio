package zserio_service_http;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Exception;
import java.lang.RuntimeException;

import zserio_runtime.ServiceInterface;

public class ZserioService
{
    public static class HttpClient implements ServiceInterface
    {
        public HttpClient(String host, int port)
        {
            this.host = host;
            this.port = port;
        }

        @Override
        public byte[] callProcedure(String procName, byte[] requestData)
        {
            HttpURLConnection connection;
            try
            {
                final URL url = new URL(protocol, host, port, "/" + procName.replace('.', '/'));
                connection = (HttpURLConnection)url.openConnection();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }

            try
            {
                connection.setDoOutput(true); // will use POST automatically
                connection.setFixedLengthStreamingMode(requestData.length);
                final DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write(requestData, 0, requestData.length);
                out.close();

                final DataInputStream in = new DataInputStream(connection.getInputStream());
                final int length = in.available();
                final byte[] responseData = new byte[length];
                in.readFully(responseData);
                in.close();
                return responseData;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }
            finally
            {
                connection.disconnect();
            }
        }

        private final String protocol = "HTTP";
        private final String host;
        private final int port;
    }
}
