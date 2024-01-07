package io.github.easy.rpc.core.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import io.github.easy.rpc.core.exception.EasyRpcException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class HessianSerializerUtil {
    public static <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            HessianOutput ho = new HessianOutput(os);
            ho.writeObject(obj);
            return os.toByteArray();
        } catch (Exception e) {
            throw new EasyRpcException(e);
        }
    }

    public static <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            HessianInput hi = new HessianInput(is);
            Object result = hi.readObject();
            return result;
        } catch (Exception e) {
            throw new EasyRpcException(e);
        }
    }
}
