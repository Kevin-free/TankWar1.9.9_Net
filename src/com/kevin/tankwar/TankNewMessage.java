package com.kevin.tankwar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMessage implements Message{
	int msgType = Message.TANK_NEW_MSG;
	Tank tank;
	TankClient tc;
	
	
	//持有引用
	public TankNewMessage (Tank tank) {
		this.tank = tank;
	}
	
	public TankNewMessage(TankClient tc) {
		this.tc = tc;
	}
	
	//发送数据
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);		
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);//将tank.id写入到输出流dos中（顺序即为id,x,y）
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP,udpPort));
		try {
			ds.send(dp);
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
	}
	
	//解析数据
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();//将输入流dis读出到id中（按dis中的顺序读出）
			//如果id等于自己，不作处理
			if(tc.mytank.id == id) {
				return;
			}		
			
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			Boolean good = dis.readBoolean();
//System.out.println("id:"+id+"-x:"+x+"-y:"+y+"-dir:"+dir+"-good:"+good);
			boolean exist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					exist = true;
					break;
				}
			}
			if(!exist) {
				TankNewMessage tnmsg = new TankNewMessage(tc.mytank);
				tc.nc.send(tnmsg);
				
				Tank t = new Tank(x, y, good, dir, tc);
				t.id = id;
				tc.tanks.add(t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}








