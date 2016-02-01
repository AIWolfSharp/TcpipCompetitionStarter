package com.carlo.starter.competition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;

public class RoleWinLoseCounter {
	Map<Role,WinLoseCounter> counters;
	/** 各役職の勝利ポイント */
	Map<Role,Double> pointMap;
	/** 全役職の勝利ポイントの合計 */
	private double totalPoint;
	/** 結果表示する時の名前。チーム名等 */
	private String name;
	public RoleWinLoseCounter(String name){
		this.name=name;
		counters=new LinkedHashMap<Role,WinLoseCounter>();
		for(Role role:Role.values()){
			if(role==Role.FREEMASON) continue;
			counters.put(role, new WinLoseCounter());
		}
	}
	public void endGame(Team winTeam,Role myRole){
		if(winTeam==myRole.getTeam()) counters.get(myRole).addWin();
		else counters.get(myRole).addLose();
		
	}
	public void setName(){
		this.name=name;
	}
	public double getRate(Role role){
		return counters.get(role).calcRate();
	}
	public int getWinCount(Role role){
		return counters.get(role).getWin();
	}
	public int getLoseCount(Role role){
		return counters.get(role).getLose();
	}
	public int getTotalCount(Role role){
		return counters.get(role).getTotal();
	}
	public Map<Role,WinLoseCounter> getCounterMap(){
		return counters;
	}
	public String getName(){
		return name;
	}
	/**
	 *  勝利ポイントの計算
	 * @param averageMap
	 */
	public void calcPointMap(Map<Role,Double> averageMap){
		pointMap=new HashMap<Role,Double>();
		totalPoint=0;
		for(Entry<Role, WinLoseCounter> entry : getCounterMap().entrySet()) {
			double point=entry.getValue().getPoint(averageMap.get(entry.getKey()));
			pointMap.put(entry.getKey(),point);
			totalPoint+=point;
		}
	}
	/**
	 *  事前にcalcPointMapを呼んでおく
	 * @param role
	 * @return 
	 */
	public double getPoint(Role role) {
		return pointMap.get(role);
	}
	public double getTotalPoint(){
		return totalPoint;
	}
}
