package com.yy.common.excel.export.grammargenerater;

import com.yy.common.excel.export.bean.Property;
import com.yy.common.excel.export.exception.ExcelExportException;
import com.yy.common.util.Cacher;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-23 10:48
 */
public class GrammarGeneratorContext {

    private List<GrammarGenerator> generators;

    private static final Cacher<String, Set<Class<? extends GrammarGenerator>>> CACHER = new Cacher<>(packageName -> {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName)));
        return reflections.getSubTypesOf(GrammarGenerator.class);
    });

    public GrammarGeneratorContext(Property property) {
        Set<Class<? extends GrammarGenerator>> classes = CACHER.value(GrammarGenerator.class.getPackage().getName());

        for (Class<? extends GrammarGenerator> c1ass : classes) {
            GrammarGenerator generator;
            try {
                generator = c1ass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelExportException("语法生成器构造失败");
            }

            boolean bool = generator.supports(property);
            if (bool) {
                if (this.generators == null) {
                    this.generators = new ArrayList<>();
                }
                this.generators.add(generator);
            }
        }

        if (this.generators == null) {
            throw new ExcelExportException("找不到支持的语法生成器");
        }
    }

    public Map<String, String> generate(Property property) {
        Map<String, String> grammarMap = new HashMap<>();
        this.generators.forEach(generator -> grammarMap.putAll(generator.generate(property)));
        return grammarMap;
    }
}
