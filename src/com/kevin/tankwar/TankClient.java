package com.kevin.tankwar;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

//坦克客户端类
public class TankClient extends Frame{

	public static final int GAME_WIDTH = 800;//常量
	public static final int GAME_HEIGHT = 600;	
	
	Image offScreenImage = null;
	
	Tank mytank = new Tank(50, 50, true , Dir.STOP,this);
	
	List<Missile> missiles = new ArrayList<Missile>();	
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();
	
	NetClient nc = new NetClient(this);
	ConnDialog dialog = new ConnDialog();
	
	//绘制坦克、子弹
	@Override
	public void paint(Graphics g) {
		g.drawString("missiles count = " + missiles.size(), 10, 50);
		g.drawString("explodes count = " + explodes.size(), 10, 70);
		g.drawString("tanks    count = " + tanks.size(), 10, 90);

		mytank.draw(g);		
		for(int i = 0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(mytank);
			m.draw(g);
		}
		for(int i = 0; i<explodes.size();i++) {
			Explode e = explodes.get(i);
			e.draw(g);			
		}
		for(int i = 0; i<tanks.size();i++) {
			Tank t = tanks.get(i);
			t.draw(g);
		}
	}
	
	@Override
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH,GAME_HEIGHT);			
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.green);
		gOffScreen.fillRect(0, 0, GAME_WIDTH,GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	//加载窗口
	public void lunchFrame() {
		/*for(int i = 0; i<10; i++) {
			tanks.add(new Tank(100+50*(i+1),50,false,Dir.D,this));
		}*/
		this.setLocation(200,100);
		this.setTitle("TankWar");
		this.setSize(GAME_WIDTH,GAME_HEIGHT);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		this.setResizable(false);//设置此窗体是否可由用户调整大小。		
		this.setBackground(Color.GREEN);//设置背景色
		
		this.addKeyListener(new KeyMonitor());
		
		setVisible(true);	
		new Thread(new PaintThread()).start();
		
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lunchFrame();
	}

	private class PaintThread implements Runnable{

		@Override
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	private class KeyMonitor extends KeyAdapter{

		@Override
		public void keyReleased(KeyEvent e) {
			mytank.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			// 按下 C,弹出对话框，进行连接
			if(key == KeyEvent.VK_C) {
				dialog.setVisible(true);
			}else {
				mytank.keyPressed(e);
			}
		}
		
		
	}

	class ConnDialog extends Dialog{
		TextField tfIP = new TextField("127.0.0.1",12);
		TextField tfPort = new TextField(""+TankServer.TCP_PORT,4);
		TextField tfMyUdpPort = new TextField("2223",4);
		Button b = new Button("确定");
		
		public ConnDialog() {
			super(TankClient.this,true);
			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("My UDP Port:"));
			this.add(tfMyUdpPort);
			this.add(b);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
				
			});
			b.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText().trim();
					int Port = Integer.parseInt(tfPort.getText().trim());
					int myUDPPort = Integer.parseInt(tfMyUdpPort.getText().trim());
					nc.setUdpPort(myUDPPort);
					nc.connect(IP, Port);
					setVisible(false);
				}
			});
		}
	}
	
	
}







