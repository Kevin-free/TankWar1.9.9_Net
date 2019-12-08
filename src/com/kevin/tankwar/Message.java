package com.kevin.tankwar;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Message {
	public static final int TANK_NEW_MSG = 1;
	public static final int TANK_MOVE_MSG = 2;
	public static final int MISSILE_NEW_MSG = 3;
	
	public void send(DatagramSocket ds,String IP,int udpPort) ;
	public void parse(DataInputStream dis);
}
