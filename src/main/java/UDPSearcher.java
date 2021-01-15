import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UDPSearcher {
    private static final int LISTEN_PORT = 30000;

    // 先接收UDP消息，再回送UDP消息
    public static void main(String[] args) throws IOException {

        // 先进行监听
        Listener listener = listen();

        // 发送广播
        sendBroadcast();

        // 输出搜索到的设备
        System.in.read();
        List<Device> devices = listener.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println(device);
        }
    }

    private static Listener listen() {
        System.out.println("UDPSearcher start listen!");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(countDownLatch, LISTEN_PORT);
        listener.start();
        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast start!");

        DatagramSocket ds = new DatagramSocket();

        // 发送消息
        byte[] sendMsgBytes = MessageCreator.buildWithPort(LISTEN_PORT).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
        sendPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        sendPacket.setPort(20000);
        ds.send(sendPacket);

        System.out.println("UDPSearcher sendBroadcast end!");
    }

    public static class Device {
        public final String ip;
        public final int port;
        public final String sn;

        public Device(String ip, int port, String sn) {
            this.ip = ip;
            this.port = port;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    public static class Listener extends Thread {
        private DatagramSocket ds;
        private final CountDownLatch countDownLatch;
        private final int listenPort;
        private boolean done = false;

        private final List<Device> devices = new ArrayList<>();

        public Listener(CountDownLatch countDownLatch, int listenPort) {
            this.countDownLatch = countDownLatch;
            this.listenPort = listenPort;
        }

        @Override
        public void run() {
            countDownLatch.countDown();
            try {
                ds = new DatagramSocket(listenPort);

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

                    String sn = MessageCreator.parseSn(msg);
                    if (sn != null) {
                        devices.add(new Device(address, port, sn));
                    }
                }
            } catch (Exception ignore) {
            } finally {
                close();
            }

            System.out.println("UDPSearcher listener end!");
        }

        private void close() {
            if (ds != null) {
                ds.close();
            }
        }

        public List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }

}
