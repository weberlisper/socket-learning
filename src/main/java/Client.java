import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket client = new Socket();
        client.setSoTimeout(3000);

        client.connect(new InetSocketAddress(InetAddress.getLocalHost(), 2000), 3000);

        System.out.println("客户端已连接到服务器：" + client.getInetAddress() + "/" + client.getPort());

        try {
            todo(client);
        } catch (Exception e) {
            System.out.println("异常断开");
        }

        client.close();
        System.out.println("与服务器：" + client.getInetAddress() + "/" + client.getPort() + "的连接已断开");
    }

    private static void todo(Socket client) throws IOException {
        // 获取键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        // 获取socket输出流
        PrintStream socketOutput = new PrintStream(client.getOutputStream());

        // 获取socket输入流
        BufferedReader socketInput = new BufferedReader(new InputStreamReader(client.getInputStream()));

        boolean flag = true;
        do {
            String writeLine = input.readLine();
            socketOutput.println(writeLine);

            String readLine = socketInput.readLine();
            if (readLine.equalsIgnoreCase("bye")) {
                flag = false;
            } else {
                System.out.println(readLine);
            }
        } while (flag);

        socketOutput.close();
        socketInput.close();
    }

}
