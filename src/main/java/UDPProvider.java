import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPProvider {
    // 先发送消息，再接收消息
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider start!");

        // 在端口号为20000的地方监听消息
        DatagramSocket socket = new DatagramSocket(20000);

        // 接收消息
        byte[] buffer = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivePacket);
        // 打印接收到的消息
        String address = receivePacket.getAddress().getHostAddress();
        int port = receivePacket.getPort();
        String msg = new String(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
        System.out.println("Receive message: " + msg + " from " + address + "/" + port);

        // 回送消息
        byte[] sendMsgBytes = ("Receive len: " + msg.length()).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
        sendPacket.setAddress(receivePacket.getAddress());
        sendPacket.setPort(port);
        socket.send(sendPacket);

        System.out.println("UDPProvider end!");
    }
}
