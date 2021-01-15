import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPSearcher {

    // 先接收UDP消息，再回送UDP消息
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher start!");

        // 在端口号为20000的地方监听消息
        DatagramSocket socket = new DatagramSocket();

        // 发送消息
        byte[] sendMsgBytes = "Hello World!".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
        sendPacket.setAddress(InetAddress.getLocalHost());
        sendPacket.setPort(20000);
        socket.send(sendPacket);

        // 接收消息
        byte[] buffer = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivePacket);
        // 打印接收到的消息
        String address = receivePacket.getAddress().getHostAddress();
        int port = receivePacket.getPort();
        String msg = new String(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
        System.out.println("Receive message: " + msg + " from " + address + "/" + port);

        System.out.println("UDPSearcher end!");
    }

}
