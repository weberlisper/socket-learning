
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = createServerSocket();

        server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 2000));

        for (; ; ) {
            Socket client = server.accept();
            System.out.println("连接客户端：" + client.getInetAddress() + "/" + client.getPort());
            new ClientHandler(client).start();
        }
    }

    private static ServerSocket createServerSocket() throws IOException {
        return new ServerSocket();
    }

    private static class ClientHandler extends Thread {
        private final Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // 获取socket输入输出流
                OutputStream outputStream = client.getOutputStream();
                InputStream inputStream = client.getInputStream();
                byte[] buffer = new byte[256];
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                int readCount = inputStream.read(buffer);
                if (readCount > 0) {
                    // byte
                    System.out.println(byteBuffer.get());

                    // char
                    System.out.println(byteBuffer.getChar());

                    // short
                    System.out.println(byteBuffer.getShort());

                    // int
                    System.out.println(byteBuffer.getInt());

                    // long
                    System.out.println(byteBuffer.getLong());

                    // bool
                    System.out.println(byteBuffer.get() == 1);

                    // float
                    System.out.println(byteBuffer.getFloat());

                    // double
                    System.out.println(byteBuffer.getDouble());

                    // string
                    int position = byteBuffer.position();
                    System.out.println(new String(buffer, position, buffer.length - position - 1));

                } else {
                    System.out.println("未收到数据: " + readCount);
                }
                outputStream.write(readCount);

                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                System.out.println("客户端：" + client.getInetAddress() + "/" + client.getPort() + "异常断开");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端：" + client.getInetAddress() + "/" + client.getPort() + "断开连接");
        }
    }

}
