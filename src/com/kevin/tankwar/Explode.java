package com.kevin.tankwar;

import java.awt.Color;
import java.awt.Graphics;

public class Explode {
	TankClient tc;
	int x,y;
	int []d = {4,7,9,14,20,35,48,30,19,8,3};
	int step = 0;
	private boolean live = true;
	
	Explode(int x,int y,TankClient tc){
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		if(!live) {
			tc.explodes.remove(this);			
			return;
		}
		if(step == d.length) {
			live = false;
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.fillOval(x, y, d[step], d[step]);
		g.setColor(c);
		step++;
	}
}
