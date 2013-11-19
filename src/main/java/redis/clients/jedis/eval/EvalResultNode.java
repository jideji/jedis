package redis.clients.jedis.eval;

import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class EvalResultNode {
    private final Object node;

    public EvalResultNode(Object node) {
	this.node = node;
    }

    public boolean isNull() {
	return node == null;
    }

    public boolean isList() {
	return node instanceof List<?>;
    }

    public List<EvalResultNode> getList() {
	if (isNull()) {
	    return null;
	}

	List<EvalResultNode> list = new ArrayList<EvalResultNode>();
	for (Object element : (List<?>) node) {
	    list.add(new EvalResultNode(element));
	}
	return list;
    }

    public boolean isLong() {
	return node instanceof Number;
    }

    public Long getLong() {
	return !isNull() ? (Long) node : null;
    }

    public boolean isByteString() {
	return node instanceof byte[];
    }

    public String getString() {
	return !isNull() ? SafeEncoder.encode((byte[]) node) : null;
    }

    public byte[] getBytes() {
	return (byte[]) node;
    }

    public BitSet getBitSet() {
	if (isNull()) {
	    return null;
	}

	BitSet bitSet = new BitSet();

	int bitSetIndex = 0;
	for (byte b : getBytes()) {
	    for (int i = Byte.SIZE - 1; i >=0; i--) {
		boolean isSet = (b >>> i & 1) == 1;
		bitSet.set(bitSetIndex, isSet);

		bitSetIndex++;
	    }
	}
	return bitSet;
    }
}
