package no.uio.master.autoscale.util;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.message.AgentMessage;

public class AgentUtils {

	/**
	 * Append configuration-variables to agent-message
	 * @param msg
	 */
	public static void appendConfigurationToMessage(AgentMessage agentMsg) {
		
		agentMsg.put("intervall_timer", Config.intervall_timer_agent);
		agentMsg.put("threshold_breach_limit", Config.threshold_breach_limit);
		agentMsg.put("min_memory_usage", Config.min_memory_usage);
		agentMsg.put("max_memory_usage", Config.max_memory_usage);
		agentMsg.put("min_free_disk_space", Config.min_free_disk_space);
		agentMsg.put("max_free_disk_space", Config.max_free_disk_space);
	}
}
