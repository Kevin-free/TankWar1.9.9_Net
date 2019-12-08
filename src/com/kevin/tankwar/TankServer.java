package com.kevin.tankwar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

//坦克服务器类
public class TankServer {
	private static int ID = 100;
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6666;
	List<Client> clients = new ArrayList<Client>();
	
	public void start(){
		
		new Thread(new UDPThread()).start();
		
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true) {
			Socket s = null;
			try {
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(IP, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);
				
				//s.close();
	System.out.println("A client connect !  Add :"+ s.getInetAddress() + ":" + s.getPort());
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				if(s != null) {
					try {
						s.close();
						s = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new TankServer().start();
	}

	private class Client{
		String IP;
		int udpPort;
		
		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
	}
	
	//UDP接收线程
	private class UDPThread implements Runnable{

		byte[] buf = new byte[1024];
		
		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
System.out.println("UDP thread started at port:" + UDP_PORT);		
			while(ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					//数据套接字接收数据包
					ds.receive(dp);
					
					for(int i = 0;i<clients.size();i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
						//ds转发数据包
						ds.send(dp);
					}
System.out.println("a packet received!");					
				} catch (IOException e) {				
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
}
