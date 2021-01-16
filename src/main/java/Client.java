import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Client {
    private static final int PORT = 2000;
    private static final int LOCAL_PORT = 2001;

    public static void main(String[] args) throws IOException {
        Socket client = createSocket();

        initSocket(client);

        client.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT), 3000);

        System.out.println("客户端已连接到服务器：" + client.getInetAddress() + "/" + client.getPort());

        try {
            todo(client);
        } catch (Exception e) {
            System.out.println("异常断开");
        }

        client.close();
        System.out.println("与服务器：" + client.getInetAddress() + "/" + client.getPort() + "的连接已断开");
    }

    private static void initSocket(Socket socket) throws SocketException {
        socket.setSoTimeout(3000);
    }

    private static Socket createSocket() throws IOException {
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), LOCAL_PORT));
        return socket;
    }

    private static void todo(Socket client) throws IOException {
        // 获取socket输入输出流
        OutputStream outputStream = client.getOutputStream();
        InputStream inputStream = client.getInputStream();
        byte[] buffer = new byte[256];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // byte
        byteBuffer.put((byte) 0x126);

        // char
        byteBuffer.putChar('a');

        // short
        byteBuffer.putShort((short) 1234);

        // int
        byteBuffer.putInt(123456);

        // long
        byteBuffer.putLong(128302348784989533L);

        // bool
        boolean flag = true;
        byteBuffer.put((byte) (flag ? 1 : 0));

        // float
        byteBuffer.putFloat(128.1892f);

        // double
        byteBuffer.putDouble(182.29384729384982);

        // string
        byteBuffer.put("Hello World!".getBytes());

        outputStream.write(buffer, 0, byteBuffer.position() + 1);

        // 读取数据
        byte[] readBuffer = new byte[128];
        int readCount = inputStream.read(readBuffer);
        if (readCount > 0) {
            System.out.println("收到数据：" + readBuffer[0]);
        } else {
            System.out.println("未收到数据: " + readCount);
        }

        outputStream.close();
        inputStream.close();
    }

}
