package me.skiincraft.discord.ousu.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CooldownManager {

	public static Map<String, Cooldown> cooldown = new HashMap<>();

	public CooldownManager() {
		super();
	}

	public void addToCooldown(String userid, int seconds) {
		long time = System.currentTimeMillis();
		if (cooldown.containsKey(userid)) {
			long lasttime = cooldown.get(userid).getEndtime();
			cooldown.remove(userid);
			cooldown.put(userid, new Cooldown(time, TimeUnit.SECONDS.toMillis(seconds) + lasttime));
			// Ele vai adicionar mais cooldown;
			return;
		}

		cooldown.put(userid, new Cooldown(time, TimeUnit.SECONDS.toMillis(seconds)));
		return;
	}

	public void setToCooldown(String userid, int seconds) {
		long time = System.currentTimeMillis();
		if (cooldown.containsKey(userid)) {
			cooldown.remove(userid);
			cooldown.put(userid, new Cooldown(time, TimeUnit.SECONDS.toMillis(seconds)));
			// Ele vai somente setar o cooldown;
			return;
		}

		cooldown.put(userid, new Cooldown(time, TimeUnit.SECONDS.toMillis(seconds)));
		return;
	}

	public void removeCooldown(String userid) {
		if (cooldown.containsKey(userid)) {
			cooldown.remove(userid);
		}
		return;
	}

	public boolean isInCooldown(String userid) {
		return cooldown.containsKey(userid);
	}

	public int cooldownSize() {
		return cooldown.size();
	}

	public void clearCooldowns() {
		cooldown.clear();
	}

	public static void start() {
		Timer t = new Timer("Users-Cooldowns");
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				if (cooldown.size() == 0) {
					return;
				}

				List<String> keys = cooldown.keySet().stream().collect(Collectors.toList());
				keys.forEach(v -> {
					Cooldown v2 = cooldown.get(v);
					if (v2.getEndtime() < System.currentTimeMillis() - v2.getStarttime()) {
						cooldown.remove(v);
					}
				});
				return;
			}
		}, 0, 200);
	}

	private class Cooldown {
		private long starttime;
		private long endtime;

		public Cooldown(long starttime, long endtime) {
			this.starttime = starttime;
			this.endtime = endtime;
		}

		public long getStarttime() {
			return starttime;
		}

		public long getEndtime() {
			return endtime;
		}

	}

}
