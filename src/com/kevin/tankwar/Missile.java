package com.kevin.tankwar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;


public class Missile {
	public static final int XSPEED = 20;
	public static final int YSPEED = 20;

	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;

	int x,y;
	Dir dir;
	
	private boolean live = true;
	
	boolean good;
	
	private TankClient tc;
	
	int tankID;
	public boolean isLive() {
		return live;
	}

	public Missile(int tankID,int x, int y, Dir dir) {
		super();
		this.tankID = tankID;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile (int tankID,int x, int y,boolean good, Dir dir, TankClient tc) {
		this(tankID,x, y,dir);
		this.good = good;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		if(!live) {
			tc.missiles.remove(this);
			return;			
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
	}

	public void move() {
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		}
	
		if(x<0 || y<0 || x>TankClient.GAME_WIDTH || y>TankClient.GAME_HEIGHT) {
			live = false;
			
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	//实现子弹是否打中坦克
	public boolean hitTank(Tank t) {
		if(this.getRect().intersects(t.getRect()) && t.isLive() && this.good!=t.isGood()) {
			t.setLive(false);
			this.live = false;
			Explode e = new Explode(x,y,tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}

	public boolean hitTanks(List<Tank> tanks) {
		for(int i = 0; i<tanks.size();i++) {
			if(hitTank(tanks.get(i))) {
				return true;
			}
		}
		return false;
	}
	
}

