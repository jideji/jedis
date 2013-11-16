package redis.clients.jedis.tests.commands;

import org.junit.Test;
import redis.clients.jedis.eval.EvalResultNode;
import redis.clients.jedis.eval.TypeSafeConverter;
import redis.clients.util.SafeEncoder;

import java.util.Collections;
import java.util.List;

public class ScriptingCommandsCustomConversionTest extends JedisCommandTestBase {

    public static final List<String> KEYS = Collections.emptyList();
    public static final List<String> ARGS = Collections.emptyList();

    @Test
    public void evalHandlesLongs() {
	EvalResultNode actual = evaluate("return 2");

	assertTrue("expected long", actual.isLong());
	assertEquals(2, actual.getLong());
    }

    @Test
    public void evalHandlesStrings() {
	EvalResultNode actual = evaluate("return 'i am a string'");

	assertTrue("expected byte string", actual.isByteString());
	assertEquals("i am a string", actual.getString());
    }

    @Test
    public void evalHandlesLists() {
	EvalResultNode actual = evaluate("return {5, 8}");

	assertTrue("expected list", actual.isList());
	List<EvalResultNode> subList = actual.getList();
	assertEquals(2, subList.size());
	assertEquals(5, subList.get(0).getLong());
	assertEquals(8, subList.get(1).getLong());
    }

    @Test
    public void evalHandlesUtf8UnsafeBytes() {
	jedis.set(SafeEncoder.encode("bits"), new byte[] {-0x60});
	EvalResultNode actual = evaluate("return redis.call('get', 'bits')");

	assertTrue("expected byte string", actual.isByteString());
	assertArrayEquals(new byte[]{-0x60}, actual.getBytes());
    }


    private EvalResultNode evaluate(String script) {
	return jedis.eval(script, new PassthroughConverter(), KEYS, ARGS);
    }

    private static class PassthroughConverter extends TypeSafeConverter<EvalResultNode> {
	@Override
	protected EvalResultNode doConvert(EvalResultNode node) {
	    return node;
	}
    }
}

