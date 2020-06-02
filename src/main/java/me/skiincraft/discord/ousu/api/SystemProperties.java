package me.skiincraft.discord.ousu.api;

import java.io.File;
import java.util.Map;

public class SystemProperties {

	public SystemProperties() {
		super();
	}
	
	public OperationalSystem getOperationalSystem() {
		return new OperationalSystem(System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
	}
	
	public Map<String, String> getEnvironment(){
		return System.getenv();
	}
	
	public RuntimeInfo getRuntimeInfo() {
	    long kilobytes = 1024;
	    long megabytes = kilobytes * 1024;

	    int cores = Runtime.getRuntime().availableProcessors();

	    float freeMemory = Runtime.getRuntime().freeMemory() / (float) megabytes;
	    float maxMemory = Runtime.getRuntime().maxMemory() / (float) megabytes;
	    float usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (float) megabytes;
	    float totalMemory = Runtime.getRuntime().totalMemory() / (float) megabytes;

		
		return new RuntimeInfo(cores, freeMemory, maxMemory, usedMemory, totalMemory);
	}
	
	public FileSystemInfo getFileSystem() {
		return new FileSystemInfo();
	}
	
	
	public static class FileSystemInfo {
		
		private String absolutePath;
		private float totalSpace;
		private float freeSpace;
		private float usageSpace;
		
		public FileSystemInfo() {
			long kilobytes = 1024;
			long megabytes = kilobytes * 1024;
			long gigabytes = megabytes * 1024;
			File[] roots = File.listRoots();
			for (File root : roots) {
				absolutePath = root.getAbsolutePath();
				totalSpace = root.getTotalSpace() / (float) gigabytes;
				freeSpace = root.getFreeSpace() / (float) gigabytes;
				usageSpace = root.getUsableSpace() / (float) gigabytes;
			}
		}
		
		/**
		 * 
		 * @param get gigabytes;
		 */
		
		public String getabsolutePath() {
			return absolutePath;
		}
		/**
		 * 
		 * @param get gigabytes;
		 */
		
		public float getTotalSpace() {
			return totalSpace;
		}
		/**
		 * 
		 * @param get gigabytes;
		 */

		public float getFreeSpace() {
			return freeSpace;
		}
		/**
		 * 
		 * @param get gigabytes;
		 */

		public float getUsageSpace() {
			return usageSpace;
		}
	}
	
	public static class RuntimeInfo {
		private int processorsCores;
		private float freeMemory;
		private float maxMemory;
		private float totalMemory;
		private float usedMemory;
		
		public RuntimeInfo(int processorsCores, float freeMemory, float maxMemory, float usedMemory, float totalMemory) {
			super();
			this.processorsCores = processorsCores;
			this.freeMemory = freeMemory;
			this.maxMemory = maxMemory;
			this.usedMemory = usedMemory;
		}
		
		public int getProcessorsCores() {
			return processorsCores;
		}
		
		/**
		 * 
		 * @param get megabytes;
		 */
		public float getFreeMemory() {
			return freeMemory;
		}
		
		/**
		 * 
		 * @param get megabytes;
		 */
		public float getMaxMemory() {
			return maxMemory;
		}
		
		/**
		 * 
		 * @param get megabytes;
		 */
		public float getUsedMemory() {
			return usedMemory;
		}
		/**
		 * 
		 * @param get megabytes;
		 */
		public float getTotalMemory() {
			return totalMemory;
		}
		
		
	}
	
	public static class OperationalSystem {
		private String name;
		private String version;
		private String architeture;
		
		public OperationalSystem(String name, String version, String architeture) {
			super();
			this.name = name;
			this.version = version;
			this.architeture = architeture;
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		public String getArchiteture() {
			return architeture;
		}
		
		
		
	}

}
