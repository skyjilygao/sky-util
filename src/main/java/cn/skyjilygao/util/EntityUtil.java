package cn.skyjilygao.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cglib.beans.BeanCopier;

public class EntityUtil {
    /**
     * 自带toString() 不好用，json strig 更直观
     * @param obj
     * @return json string
     */
    public static String toJsonString(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    /**
     * 属性拷贝，有相同属性时。将source中的值 复制到target对象中
     * @param source 源对象
     * @param target 目标对象，引用被返回。
     */
    public static void copier(Object source, Object target){
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }
}