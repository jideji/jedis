package redis.clients.jedis.eval;

public class EvalResultNodeConverter implements EvalResultConverter<EvalResultNode> {
    @Override
    public final EvalResultNode convert(Object result) {
	return new EvalResultNode(result);
    }
}
