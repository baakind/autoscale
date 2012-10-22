package no.uio.master.autoscale.util;


/**
 * Holds node and corresponding score for scaler-algorithm.
 * 
 * @author andreas
 */
public class HostWeight implements Comparable<HostWeight> {

	private String host;
	private Integer score;
	private Scale scale;

	public HostWeight() {
	}

	public HostWeight(String h) {
		host = h;
		score = 0;
	}

	public HostWeight(String h, Integer s) {
		host = h;
		score = s;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	

	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostWeight other = (HostWeight) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HostWeight [host=" + host + ", score=" + score + "]";
	}

	@Override
	public int compareTo(HostWeight other) {
		return this.getScore().compareTo(other.getScore());
	}

}
