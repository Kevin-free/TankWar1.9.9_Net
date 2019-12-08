package com.kevin.tankwar;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

//网络客户端类
public class NetClient {
	TankClient tc;
	private int UDP_PORT_START = 2230;
	private int udpPort;
	
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	DatagramSocket ds = null;
		
	public NetClient (TankClient tc){
		this.tc = tc;
	}
	
	public void connect(String IP, int port) {
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Socket s = null;
		try {
			s = new Socket(IP, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.mytank.id = id;
			if(id%2 == 0) {
				tc.mytank.good = false;
			}else {
				tc.mytank.good = true;
			}
System.out.println("Connectes to server!and sever gives me a ID:" + id);			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	
		TankNewMessage msg = new TankNewMessage(tc.mytank);
		send(msg);
		
		new Thread(new UDPRecivedThread()).start();
	}
	
	
	public void send(Message msg) {
		msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);
	}
	
	private class UDPRecivedThread implements Runnable{

		byte [] buf = new byte[1024];
		
		@Override
		public void run() {

			while(ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);			
					parse(dp);
System.out.println("a packet received from server!");					
				} catch (IOException e) {				
					e.printStackTrace();
				}
				
			}
		
		}

		//解析数据包
		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0 ,dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = null;
			switch (msgType) {
			case Message.TANK_NEW_MSG:
				msg = new TankNewMessage(tc);
				msg.parse(dis);
				break;
			case Message.TANK_MOVE_MSG:
				msg = new TankMoveMessage(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Message.MISSILE_NEW_MSG:
				msg = new MissileNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			default:
				break;
			}
			
		}
		
	}
	
	
	
	
	
}
