import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.UUID;

public class UDPProvider {
    // 先发送消息，再接收消息
    public static void main(String[] args) throws IOException {
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        System.in.read();
        provider.exit();
    }

    public static class Provider extends Thread {
        private DatagramSocket ds;
        private final String sn;
        private boolean done = false;

        public Provider(String sn) {
            this.sn = sn;
        }

        @Override
        public void run() {
            System.out.println("UDPProvider start!");

            try {
                // 在端口号为20000的地方监听消息
                ds = new DatagramSocket(20000);

                while (!done) {
                    // 接收消息
                    byte[] buffer = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    ds.receive(receivePacket);
                    // 打印接收到的消息
                    String address = receivePacket.getAddress().getHostAddress();
                    int port = receivePacket.getPort();
                    String msg = new String(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
                    System.out.println("Receive message: " + msg + " from " + address + "/" + port);

                    int responsePort = MessageCreator.parsePort(msg);
                    if (responsePort == -1) {
                        continue;
                    }

                    // 回送消息
                    byte[] sendMsgBytes = MessageCreator.buildWithSn(sn).getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
                    sendPacket.setAddress(receivePacket.getAddress());
                    sendPacket.setPort(responsePort);
                    ds.send(sendPacket);
                }
            } catch (Exception ignore) {
            } finally {
                close();
            }

            System.out.println("UDPProvider end!");
        }

        private void close() {
            if (ds != null) {
                ds.close();
            }
        }

        public void exit() {
            done = true;
            close();
        }
    }
}
