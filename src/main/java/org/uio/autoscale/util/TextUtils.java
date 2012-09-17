package org.uio.autoscale.util;

/**
 * Handling text-transformations
 * @author toraba
 */
public class TextUtils {

	/**
	 * Generate a single string from multiple hosts, as a CSV-list.
	 * @param defaultHosts
	 * @return
	 */
	public static String generateHostsList(String... defaultHosts) {
		String hosts = "";
		for(int i = 0; i < defaultHosts.length; i++) {
			hosts += defaultHosts[i] + ",";
		}
		hosts = hosts.substring(0, hosts.length()-1);
		return hosts;
	}
}
