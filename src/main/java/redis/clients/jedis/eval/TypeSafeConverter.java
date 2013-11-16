package redis.clients.jedis.eval;

public abstract class TypeSafeConverter<T> implements EvalResultConverter<T> {

    @Override
    public final T convert(Object result) {
	return doConvert(new EvalResultNode(result));
    }

    protected abstract T doConvert(EvalResultNode node);
}
