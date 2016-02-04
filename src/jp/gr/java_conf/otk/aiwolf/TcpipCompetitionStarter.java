package jp.gr.java_conf.otk.aiwolf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.net.TcpipClient;
import org.aiwolf.common.util.CalendarTools;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.TcpipServer;
import org.aiwolf.server.util.FileGameLogger;

import com.carlo.starter.competition.RoleWinLoseCounter;

/**
 * TCP/IP version of Competition Starter
 * 
 * @author (Modifier)Takashi OTSUKI
 *
 */
public class TcpipCompetitionStarter {
	private static ArrayList<Object> playerList = new ArrayList<Object>();

	private int playerNum = 15;
	private int gameNum = 1;
	private int port = 10000;
	private HashMap<Agent, RoleWinLoseCounter> winLoseCounterMap = new HashMap<Agent, RoleWinLoseCounter>();
	private HashMap<Role, Double> averageMap = new HashMap<Role, Double>();

	/**
	 * 
	 * @param port
	 * @param gameNum
	 */
	public TcpipCompetitionStarter(int playerNum, int gameNum, int port) {
		this.playerNum = playerNum;
		this.gameNum = gameNum;
		this.port = port;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayerNum() {
		return playerNum;
	}

	/**
	 * 
	 * @param isShowConsoleLog
	 *            人狼ゲームのコンソールログを表示するか
	 * @param isSaveGameLog
	 *            ゲームログを保存するか
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	public void gameStart(boolean isShowConsoleLog, boolean isSaveGameLog) throws InstantiationException, IllegalAccessException,
			InterruptedException {
		GameSetting gameSetting = GameSetting.getDefaultGame(getPlayerNum());
		TcpipServer gameServer = new TcpipServer(port, getPlayerNum(), gameSetting);
		System.out.printf("Start AIWolf Server port:%d playerNum:%d\n", port, playerNum);

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					gameServer.waitForConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread t = new Thread(r);
		t.start();

		// 事前登録済プレイヤーをサーバーに接続
		for (int i = 0; i < playerList.size(); i++) {
			if (i < playerNum) {
				new TcpipClient("localhost", port, null).connect((Player) ((Class) playerList.get(i)).newInstance());
			}
		}

		t.join();

		System.out.printf("Start Competition\n");

		for (Agent agent : gameServer.getConnectedAgentList()) {
			winLoseCounterMap.put(agent, new RoleWinLoseCounter(gameServer.requestName(agent)));
		}

		// game
		for (int i = 0; i < gameNum; i++) {
			AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
			game.setShowConsoleLog(isShowConsoleLog);
			if (isSaveGameLog) {
				String timeString = CalendarTools.toDateTime(System.currentTimeMillis()).replaceAll("[\\s-/:]", "");
				File logFile = new File(String.format("%s/aiwolfGame%s_%d.log", "./log/", timeString, i));
				try {
					game.setGameLogger(new FileGameLogger(logFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			game.start();

			// 勝敗結果格納
			for (Agent agent : game.getGameData().getAgentList()) {
				winLoseCounterMap.get(agent).endGame(game.getWinner(), game.getGameData().getRole(agent));
			}
		}
		gameServer.close();

		// 結果処理
		calcResult();
	}

	public void printwinLoseCounterMap() throws InterruptedException {
		Thread.sleep(1000);
		System.out.println("PLAYER   \tBODYGU\tMEDIUM\tPOSSES\t  SEER\tVILLAG\tWEREWO\t TOTAL");
		for (Entry<Agent, RoleWinLoseCounter> entry : winLoseCounterMap.entrySet()) {
			String fullName = entry.getValue().getName();
			System.out.printf("%9.9s", entry.getKey().toString());
			for (Role role : Role.values()) {
				if (role == Role.FREEMASON)
					continue;
				System.out.printf("\t% 6.2f", entry.getValue().getPoint(role));
			}
			System.out.printf("\t% 6.2f\t(%s)\n", entry.getValue().getTotalPoint(), fullName);
		}
	}

	/** 直下にcsvフォルダがなければ作り、csvを保存する */
	public void writeToCSVFile() {
		FileWriter fw;

		File dir = new File("./csv");
		if (!dir.exists()) {
			dir.mkdir();
		}

		String timeString = CalendarTools.toDateTime(System.currentTimeMillis()).replaceAll("[\\s-/:]", "");
		try {
			fw = new FileWriter("csv/" + timeString + ".csv", false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println("Num. of games," + gameNum);
			pw.println("Deviation");
			pw.println("Name,Bodyguard,Medium,Possessed,Seer,Villager,Werewolf,Total");
			for (Entry<Agent, RoleWinLoseCounter> entry : winLoseCounterMap.entrySet()) {
				String[] names = entry.getValue().getName().split("\\.");
				String name = names[names.length - 1];
				pw.print(name);
				for (Role role : Role.values()) {
					if (role == Role.FREEMASON)
						continue;
					pw.printf(",%.2f", entry.getValue().getPoint(role));
				}
				pw.printf(",%.2f\n", entry.getValue().getTotalPoint());
			}
			pw.println("WPCT");
			pw.println("Name,Bodyguard,Medium,Possessed,Seer,Villager,Werewolf");
			for (Entry<Agent, RoleWinLoseCounter> entry : winLoseCounterMap.entrySet()) {
				String[] names = entry.getValue().getName().split("\\.");
				String name = names[names.length - 1];
				pw.print(name);
				for (Role role : Role.values()) {
					if (role == Role.FREEMASON)
						continue;
					pw.printf(",%.2f", entry.getValue().getRate(role));
				}
				pw.println();
			}
			pw.println("Num. of wins");
			pw.println("Name,Bodyguard,Medium,Possessed,Seer,Villager,Werewolf");
			for (Entry<Agent, RoleWinLoseCounter> entry : winLoseCounterMap.entrySet()) {
				String[] names = entry.getValue().getName().split("\\.");
				String name = names[names.length - 1];
				pw.print(name);
				for (Role role : Role.values()) {
					if (role == Role.FREEMASON)
						continue;
					pw.print("," + entry.getValue().getWinCount(role) + "/" + entry.getValue().getTotalCount(role));
				}
				pw.println();
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void calcResult() {
		calcAverage();
		for (Entry<Agent, RoleWinLoseCounter> entry : winLoseCounterMap.entrySet()) {
			entry.getValue().calcPointMap(averageMap);
		}
	}

	private void calcAverage() {
		averageMap = new HashMap<Role, Double>();
		for (Role role : Role.values()) {
			if (role == Role.FREEMASON)
				continue;
			int div = getPlayerNum();
			double tmp = 0;
			for (Entry<Agent, RoleWinLoseCounter> classEntry : winLoseCounterMap.entrySet()) {
				tmp += classEntry.getValue().getRate(role);
			}
			double average = tmp / div;

			averageMap.put(role, average);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
		int playerNum = 15;
		int gameNum = 1000;
		int port = 10000;
		boolean useInternal = true; // ハードコーディングしたプレイヤーを使うかどうか

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-n")) {
					i++;
					playerNum = Integer.parseInt(args[i]);
				} else if (args[i].equals("-g")) {
					i++;
					gameNum = Integer.parseInt(args[i]);
				} else if (args[i].equals("-p")) {
					i++;
					port = Integer.parseInt(args[i]);
				} else if (args[i].equals("-e")) {
					useInternal = false;
				}
			}
		}
		if (port < 0) {
			System.exit(-1);
		}

		TcpipCompetitionStarter starter = new TcpipCompetitionStarter(playerNum, gameNum, port);

		if (useInternal) {
			playerList.add(Class.forName("org.aiwolf.client.base.smpl.SampleRoleAssignPlayer"));
			// playerList.add(Class.forName("com.yy.player.YYRoleAssignPlayer"));
			// playerList.add(Class.forName("jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer"));
			// playerList.add(Class.forName("org.aiwolf.kajiClient.LearningPlayer.KajiRoleAssignPlayer"));
			playerList.add(Class.forName("com.gmail.jinro.noppo.players.RoleAssignPlayer"));
			// playerList.add(Class.forName("org.aiwolf.Satsuki.LearningPlayer.AIWolfMain"));
			playerList.add(Class.forName("jp.ac.shibaura_it.ma15082.WasabiPlayer"));
			// playerList.add(Class.forName("takata.player.TakataRoleAssignPlayer"));
			// playerList.add(Class.forName("ipa.myAgent.IPARoleAssignPlayer"));
			playerList.add(Class.forName("org.aiwolf.iace10442.ChipRoleAssignPlayer"));
			playerList.add(Class.forName("kainoueAgent.MyRoleAssignPlayer"));
			// playerList.add(Class.forName("jp.ac.aitech.k13009kk.aiwolf.client.player.AndoRoleAssignPlayer"));
			playerList.add(Class.forName("com.github.haretaro.pingwo.role.PingwoRoleAssignPlayer"));
			playerList.add(Class.forName("com.gmail.the.seventh.layers.RoleAssignPlayer"));
			// playerList.add(Class.forName("jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriRoleAssignPlayer"));
			// playerList.add(Class.forName("com.gmail.octobersky.MyRoleAssignPlayer"));
			// playerList.add(Class.forName("com.canvassoft.Agent.CanvasRoleAssignPlayer"));
		}

		starter.gameStart(false, true);
		starter.printwinLoseCounterMap();
		starter.writeToCSVFile();
	}

}
