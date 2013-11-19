package redis.clients.jedis.tests.commands;

import org.junit.Test;
import redis.clients.jedis.eval.EvalResultNode;
import redis.clients.util.SafeEncoder;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class ScriptingCommandsCustomConversionTest extends JedisCommandTestBase {

    private static final List<String> KEYS = Collections.emptyList();
    private static final List<String> ARGS = Collections.emptyList();

    @Test
    public void evalHandlesNulls() {
	EvalResultNode actual = eval("return nil");

	assertTrue("expected null", actual.isNull());
	assertNull("expected null list", actual.getList());
	assertNull("expected null long", actual.getLong());
	assertNull("expected null byte array", actual.getBytes());
	assertNull("expected null bitset", actual.getBitSet());
	assertNull("expected null string", actual.getString());
    }

    @Test
    public void evalHandlesLongs() {
	EvalResultNode actual = eval("return 2");

	assertTrue("expected long", actual.isLong());
	assertEquals((Long) 2L, actual.getLong());
    }

    @Test
    public void evalshaHandlesLongs() {
	EvalResultNode actual = evalsha("return 2");

	assertTrue("expected long", actual.isLong());
	assertEquals((Long) 2L, actual.getLong());
    }

    @Test
    public void evalHandlesStrings() {
	EvalResultNode actual = eval("return 'i am a string'");

	assertTrue("expected byte string", actual.isByteString());
	assertEquals("i am a string", actual.getString());
    }

    @Test
    public void evalshaHandlesStrings() {
	EvalResultNode actual = evalsha("return 'i am a string'");

	assertTrue("expected byte string", actual.isByteString());
	assertEquals("i am a string", actual.getString());
    }

    @Test
    public void evalHandlesLists() {
	EvalResultNode actual = eval("return {5, 8}");

	assertTrue("expected list", actual.isList());
	List<EvalResultNode> subList = actual.getList();
	assertEquals(2, subList.size());
	assertEquals((Long) 5L, subList.get(0).getLong());
	assertEquals((Long) 8L, subList.get(1).getLong());
    }

    @Test
    public void evalshaHandlesLists() {
	EvalResultNode actual = evalsha("return {5, 8}");

	assertTrue("expected list", actual.isList());
	List<EvalResultNode> subList = actual.getList();
	assertEquals(2, subList.size());
	assertEquals((Long) 5L, subList.get(0).getLong());
	assertEquals((Long) 8L, subList.get(1).getLong());
    }

    @Test
    public void evalHandlesUtf8UnsafeBytes() {
	jedis.set(SafeEncoder.encode("bits"), new byte[] {-0x60});
	EvalResultNode actual = eval("return redis.call('get', 'bits')");

	assertTrue("expected byte string", actual.isByteString());
	assertArrayEquals(new byte[]{-0x60}, actual.getBytes());
    }

    @Test
    public void evalshaHandlesUtf8UnsafeBytes() {
	jedis.set(SafeEncoder.encode("bits"), new byte[] {-0x60});
	EvalResultNode actual = evalsha("return redis.call('get', 'bits')");

	assertTrue("expected byte string", actual.isByteString());
	assertArrayEquals(new byte[]{-0x60}, actual.getBytes());
    }

    @Test
    public void evalHandlesBitSets() {
	setBitsInRedis("bits", 0, 2, 8);
	EvalResultNode actual = eval("return redis.call('get', 'bits')");

	assertTrue("expected byte string", actual.isByteString());
	assertEquals(bitSet(0, 2, 8), actual.getBitSet());
    }

    @Test
    public void evalshaHandlesBitSets() {
	setBitsInRedis("bits", 0, 2, 8);
	EvalResultNode actual = evalsha("return redis.call('get', 'bits')");

	assertTrue("expected byte string", actual.isByteString());
	assertEquals(bitSet(0, 2, 8), actual.getBitSet());
    }


    private void setBitsInRedis(String key, int... bits) {
	for (int bit : bits) {
	    jedis.setbit(key, bit, true);
	}
    }

    private BitSet bitSet(int... bits) {
	BitSet bitSet = new BitSet();
	for (int bit : bits) {
	    bitSet.set(bit);
	}
	return bitSet;
    }

    private EvalResultNode eval(String script) {
	return jedis.evalResultNode(script, KEYS, ARGS);
    }

    private EvalResultNode evalsha(String script) {
	String sha1 = jedis.scriptLoad(script);
	return jedis.evalshaResultNode(sha1, KEYS, ARGS);
    }
}
