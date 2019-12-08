package com.kevin.tankwar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Message {
	int msgType = Message.MISSILE_NEW_MSG;
	Missile m;
	TankClient tc = new TankClient();
	
	public MissileNewMsg(Missile m) {
		this.m = m;
	}
	
	public MissileNewMsg(TankClient tc) {
		this.tc = tc;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);		
		try {
			dos.writeInt(msgType);
			dos.writeInt(m.tankID);
			dos.writeInt(m.x);
			dos.writeInt(m.y);
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.good);
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

	@Override
	public void parse(DataInputStream dis) {

		try {
			int tankID = dis.readInt();
			if(tankID==tc.mytank.id) {
				return ;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			Missile m = new Missile(tankID,x, y, good, dir, tc);
			tc.missiles.add(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
