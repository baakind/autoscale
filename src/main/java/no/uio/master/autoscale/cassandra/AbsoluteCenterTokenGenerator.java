package no.uio.master.autoscale.cassandra;

import java.math.BigInteger;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.uio.master.autoscale.node.TokenGenerator;

/**
 * Generate a token that is absolute centered between a and b.
 * @author andreas
 */
public class AbsoluteCenterTokenGenerator implements TokenGenerator<BigInteger> {
	private static Logger LOG = LoggerFactory.getLogger(AbsoluteCenterTokenGenerator.class);

	@Override
	public BigInteger generateToken(BigInteger a, BigInteger b) {
		BigInteger newToken = a.add(b);
		newToken = newToken.divide(new BigInteger("2"));
		LOG.debug("A: " + a + ", B: " + b + ", New: " + newToken);
		return newToken;
	}

}
