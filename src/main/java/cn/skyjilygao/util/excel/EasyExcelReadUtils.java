package cn.skyjilygao.util.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson2.JSON.toJSONString;

/**
 * 读取表格
 *
 * @author skyjilygao
 * @since 20240530
 */
@Slf4j
@Getter
public class EasyExcelReadUtils<T> extends AnalysisEventListener<T> {
    protected Class<T> clazz;
    private final List<T> list = new ArrayList<>();

    private EasyExcelReadUtils() {
    }

    public EasyExcelReadUtils(Class<T> clazz) {
        log.info("加载数据start...");
        this.clazz = clazz;
    }

    public List<T> read(File file) {
        return read(file, null);
    }

    public List<T> read(File file, String sheetName) {
        log.info(">>>>> Read excel file：{}", file.getAbsolutePath());
        EasyExcelFactory.read(file, this.getClazz(), this).sheet(sheetName).doRead();
        return this.getList();
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        String sheetName = readSheetHolder.getSheetName();
        Integer rowIndex = readSheetHolder.getRowIndex();
        //每读取一条数据就调用该方法一次，我这里没有与数据库进行交互，纯输出读取到的对象
        log.info(">>>>> sheetName:{}, rowIndex:{}, data:{}", sheetName, rowIndex, toJSONString(data));
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //读取结束会调用该方法
        log.info("加载数据end...");
    }
}
