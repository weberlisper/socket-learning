import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2000);

        for (; ; ) {
            Socket client = server.accept();
            System.out.println("连接客户端：" + client.getInetAddress() + "/" + client.getPort());
            new ClientHandler(client).start();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // 获取socket输出流
                PrintStream socketOutput = new PrintStream(client.getOutputStream());

                // 获取socket输入流
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(client.getInputStream()));

                boolean flag = true;
                do {
                    String readLine = socketInput.readLine();
                    if (readLine.equalsIgnoreCase("bye")) {
                        socketOutput.println("bye");
                        flag = false;
                    } else {
                        System.out.println(readLine);
                        socketOutput.println("回送：" + readLine.length());
                    }
                } while (flag);

                socketOutput.close();
                socketInput.close();
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
