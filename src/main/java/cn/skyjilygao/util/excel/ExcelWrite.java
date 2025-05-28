package cn.skyjilygao.util.excel;

import java.lang.annotation.*;

/**
 * 写excel 数据注解
 *
 * @author skyjilygao
 * @date 20201023
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelWrite {
    /**
     * field 值 类型
     *
     * @return
     */
    Class type() default Object.class;

    /**
     * 当前列的列名
     *
     * @return
     */
    String columnName() default "";

    /**
     * 浮点类型时保留小数点位数：默认2位
     *
     * @return
     */
    int scale() default 2;

    /**
     * 百分百显示: 示例："0.00%"
     *
     * @return
     */
    String dataFormart() default "";

}
