package redis.clients.jedis.eval;

import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;

public class DefaultEvalResultConverter implements EvalResultConverter<Object> {
    @Override
    public Object convert(Object result) {
	if (result instanceof byte[])
	    return SafeEncoder.encode((byte[]) result);

	if (result instanceof List<?>) {
	    List<?> list = (List<?>) result;
	    List<Object> listResult = new ArrayList<Object>(list.size());
	    for (Object bin : list) {
		listResult.add(convert(bin));
	    }

	    return listResult;
	}

	return result;
    }
}
