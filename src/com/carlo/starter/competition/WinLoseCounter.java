package com.carlo.starter.competition;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;

public class WinLoseCounter {
	private int win;
	private int lose;
	public WinLoseCounter(){
		win=0;
		lose=0;
	}
	public void addWin(){
		win+=1;
	}
	public void addLose(){
		lose+=1;
	}
	public int getWin(){
		return win;
	}
	public int getLose(){
		return lose;
	}
	public int getTotal(){
		return win+lose;
	}
	/** 勝利ポイントの計算　勝率-平均勝率 */
	public double getPoint(double average){
		return calcRate()-average;
	}
	/** 勝率を計算して返す */
	public double calcRate(){
		int b=lose+win;
		if(b==0) return 0;
		else return (double)win/b*100;
	}
	public String toString(){
		return win+"/"+(lose+win);
	}

}
