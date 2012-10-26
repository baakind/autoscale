package no.uio.master.autoscale.token;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map.Entry;

public class BigIntegerTokenComparator implements Comparator<Entry<String, String>> {

	@Override
	public int compare(Entry<String, String> thisEntry, Entry<String, String> otherEntry) {
		BigInteger thisToken = new BigInteger(thisEntry.getKey());
		BigInteger otherToken = new BigInteger(otherEntry.getKey());
		
		return thisToken.compareTo(otherToken);
	}

}
