package redis.clients.jedis.eval;

public interface EvalResultConverter<T> {
    T convert(Object result);
}
