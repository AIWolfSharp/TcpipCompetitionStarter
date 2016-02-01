package com.carlo.starter.competition;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Role;

public class RoleManager {
	/**
	 * GameSettingクラスからコピペ
	 * Num of each roles.
	 * Bodyguard, FreeMason, Medium, Possessed, seer, villager, werewolf
	 */
	static private int[][] roleNumArray = {
		{},//0
		{},//1
		{},//2
		{},//3
		{0, 0, 0, 0, 1, 2, 1},//4
		{0, 0, 0, 1, 1, 2, 1},//5
		{0, 0, 0, 0, 1, 3, 2},//6
		{0, 0, 0, 0, 1, 4, 2}, //7
		{0, 0, 1, 0, 1, 4, 2}, //8
		{0, 0, 1, 0, 1, 5, 2}, //9
		{1, 0, 1, 0, 1, 5, 2}, //10
		{1, 0, 1, 1, 1, 5, 2}, //11
		{1, 0, 1, 1, 1, 6, 2}, //12
		{1, 0, 1, 1, 1, 7, 2}, //13
		{1, 0, 1, 1, 1, 7, 3}, //14
		{1, 0, 1, 1, 1, 8, 3}, //15
		{1, 0, 1, 1, 1, 9, 3}, //16
		{1, 0, 1, 1, 1, 10, 3}, //17
		{1, 0, 1, 1, 1, 11, 3}, //18
	};
	private int playerNum;
	public RoleManager(int playerNum){
		this.playerNum=playerNum;
	}
	public ArrayList<Role> getRoleList(){
		ArrayList<Role> roles=new ArrayList<Role>();
		int i=0;
		for(Role role:Role.values()){
			int num=roleNumArray[playerNum][i];
			for(int j=0;j<num;j++){
				roles.add(role);
			}
			i++;
		}
		return roles;
	}

}
