package com.yy.common.excel.export;

import cn.hutool.core.util.ReUtil;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.bean.Property;
import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.common.excel.export.exception.ExcelExportException;
import com.yy.common.excel.export.grammargenerater.GrammarGeneratorContext;
import com.yy.common.excel.export.utils.PropertyUtils;
import com.yy.common.excel.StringUtil;
import com.yy.common.util.ValidatorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.yy.common.excel.export.enums.Keyword.*;
import static com.yy.common.excel.export.enums.Regex.*;
import static com.yy.common.util.ValidatorUtils.FieldBean;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 13:55
 */
public class ExcelExporter {

    private String templatePath;

    private Consumer<XSSFWorkbook> preHandle;

    private Consumer<XSSFWorkbook> postHandle;

    private ExcelExporter() {
    }

    public static ExcelExporterBuilder newBuilder() {
        return new ExcelExporterBuilder();
    }

    /**
     * 复制模板sheet
     *
     * @param workbook
     * @param sheetIndex
     * @return
     */
    public static int copySheet(XSSFWorkbook workbook, int sheetIndex, String newSheetName) {
        XSSFSheet newSheet = workbook.cloneSheet(sheetIndex);

        int newSheetIndex = workbook.getSheetIndex(newSheet);
        workbook.setSheetName(newSheetIndex, newSheetName);

        return newSheetIndex;
    }

    private static List<Property> getProperties(SheetMapping mapping) {
        List<Property> properties = PropertyUtils.getProperties(mapping);
        properties = properties.stream().filter(property -> property.getValue() != null).collect(Collectors.toList());
        properties.forEach(PropertyUtils::getChildrenRecursively);
        return properties;
    }

    private static void generateGrammars(List<Property> properties, Map<String, String> grammarMap) {
        for (Property property : properties) {
            GrammarGeneratorContext context = new GrammarGeneratorContext(property);
            grammarMap.putAll(context.generate(property));

            if (property.getChildren() != null) {
                generateGrammars(property.getChildren(), grammarMap);
            }
        }
    }

    private static Map<String, List<String>> generateArrayGrammar(Map<String, String> grammarMap) {
        List<String> rgxGrammars = new ArrayList<>();
        grammarMap.keySet().stream()
                .map(grammar -> {
                    // 取元素起始/结束符命名契约：保证该语句逻辑正确
                    int numberOfIndexer = ReUtil.count(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), UNSIGNED_INTEGER_STRICT.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), grammar);
                    return Pair.of(grammar, numberOfIndexer);
                })
                .forEach(tuple -> {
                    String grammar = tuple.getLeft();
                    int numberOfIndexer = tuple.getRight();

                    for (int i = 1; i <= numberOfIndexer; i++) {
                        rgxGrammars.add(format(
                                "%s%s%s",
                                "^",
                                StringUtil.replaceNthMatch(
                                        ReUtil.escape(grammar),
                                        format("%s%s%s", ReUtil.escape(ELEMENT_START.getCodeAfterEscapeRegexCharacters()), UNSIGNED_INTEGER_STRICT.getCode(), ReUtil.escape(ELEMENT_END.getCodeAfterEscapeRegexCharacters())),
                                        i,
                                        format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), UNSIGNED_INTEGER_STRICT.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters())
                                ),
                                "$"
                        ));
                    }
                });

        Map<String, List<String>> result = new HashMap<>();
        rgxGrammars.forEach(rgxGrammar -> {
            List<String> values = grammarMap.entrySet().stream()
                    .filter(entry -> ReUtil.isMatch(rgxGrammar, entry.getKey()))
                    .sorted(Comparator.comparing(entry -> Integer.parseInt(entry.getKey().replaceAll(NOT_INTEGER.getCode(), EMPTY))))
                    .map(Map.Entry::getValue).collect(Collectors.toList());

            result.put(
                    StringUtil.unescape(
                            rgxGrammar.replace(
                                            format(
                                                    "%s%s%s",
                                                    ELEMENT_START.getCodeAfterEscapeRegexCharacters(),
                                                    UNSIGNED_INTEGER_STRICT.getCode(),
                                                    ELEMENT_END.getCodeAfterEscapeRegexCharacters()
                                            ),
                                            format(
                                                    "%s%s%s",
                                                    ELEMENT_START.getCode(),
                                                    INDEX_WILDCARD.getCode(),
                                                    ELEMENT_END.getCode()
                                            )
                                    )
                                    .replace("^", EMPTY)
                                    .replace("$", EMPTY)
                    ),
                    values
            );
        });

        return result;
    }

    private static void expandPlaceholders(XSSFSheet sheet, Map<String, List<String>> grammars) {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) continue;

            for (int j = 0; j <= row.getLastCellNum(); j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null) continue;
                if (cell.getCellType() != STRING) continue;
                String cellValue;
                if ((cellValue = cell.getStringCellValue()).isEmpty()) continue;
                List<String> placeholderWithoutWrappers;
                if ((placeholderWithoutWrappers = ReUtil.findAllGroup1(PLACEHOLDER.getCode(), cellValue)).isEmpty())
                    continue;
                List<String> verticalPlaceholderWithoutWrappers = placeholderWithoutWrappers.stream().filter(placeholder -> !ReUtil.findAllGroup0(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), placeholder).isEmpty()).collect(Collectors.toList());
                List<String> horizontalPlaceholderWithoutWrappers = placeholderWithoutWrappers.stream().filter(placeholder -> !ReUtil.findAllGroup0(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), placeholder).isEmpty()).collect(Collectors.toList());
                verticalPlaceholderWithoutWrappers = verticalPlaceholderWithoutWrappers.stream().filter(placeholder -> grammars.keySet().stream()
                        .anyMatch(v2 -> placeholder.replaceAll(
                                                format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()),
                                                format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode())
                                        )
                                        .equals(v2)
                        )).collect(Collectors.toList());
                horizontalPlaceholderWithoutWrappers = horizontalPlaceholderWithoutWrappers.stream().filter(placeholder -> grammars.keySet().stream()
                        .anyMatch(v2 -> placeholder.replaceAll(
                                                format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()),
                                                format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode())
                                        )
                                        .equals(v2)
                        )).collect(Collectors.toList());

                for (String value : verticalPlaceholderWithoutWrappers) {
                    int numberOfReservedRows = 0;
                    List<String> reservedStrList = ReUtil.findAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), value, 2);
                    if (!reservedStrList.isEmpty()) {
                        String reservedStr = reservedStrList.get(0);
                        numberOfReservedRows = StringUtils.isBlank(reservedStr) ? 0 : Integer.parseInt(reservedStr);
                    }

                    int start = i + numberOfReservedRows + 1;
                    int end = sheet.getLastRowNum();
                    int offset = grammars.get(value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode()))).size() - numberOfReservedRows - 1;

                    if (start <= end) {
                        for (int k = end; k >= start; k--) {
                            XSSFCell fromCell = POIReadUtils.getCell(sheet, k, cell.getColumnIndex());
                            XSSFCell toCell = POIReadUtils.getCell(sheet, k + offset, cell.getColumnIndex());
                            toCell.setCellStyle(fromCell.getCellStyle());
                            toCell.setCellValue(fromCell.getStringCellValue());
                        }
                    }

                    for (int k = start; k < start + offset; k++) {
                        XSSFCell fromCell = POIReadUtils.getCell(sheet, start - 1, cell.getColumnIndex());
                        XSSFCell toCell = POIReadUtils.getCell(sheet, k, cell.getColumnIndex());
                        toCell.setCellStyle(fromCell.getCellStyle());
                        toCell.setCellValue(fromCell.getStringCellValue());
                    }

                    for (int k = 0; k < grammars.get(value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode()))).size(); k++) {
                        XSSFCell tempCell = POIReadUtils.getCell(sheet, cell.getRowIndex() + k, cell.getColumnIndex());
                        String newCellValue = StringUtils.replace(cellValue, value, value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), VERTICAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), "[" + k + "]"), 1);
                        tempCell.setCellValue(newCellValue);
                    }
                }

                for (String value : horizontalPlaceholderWithoutWrappers) {
                    int numberOfReservedColumns = 0;
                    List<String> reservedStrList = ReUtil.findAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), value, 2);
                    if (!reservedStrList.isEmpty()) {
                        String reservedStr = reservedStrList.get(0);
                        numberOfReservedColumns = StringUtils.isBlank(reservedStr) ? 0 : Integer.parseInt(reservedStr);
                    }

                    int start = j + numberOfReservedColumns + 1;
                    int end = row.getLastCellNum();
                    int offset = grammars.get(value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode()))).size() - numberOfReservedColumns - 1;

                    if (start <= end) {
                        for (int k = end; k >= start; k--) {
                            XSSFCell fromCell = POIReadUtils.getCell(sheet, i, k);
                            XSSFCell toCell = POIReadUtils.getCell(sheet, i, k + offset);
                            toCell.setCellStyle(fromCell.getCellStyle());
                            toCell.setCellValue(fromCell.getStringCellValue());
                        }
                    }

                    for (int k = start; k < start + offset; k++) {
                        XSSFCell fromCell = POIReadUtils.getCell(sheet, i, start - 1);
                        XSSFCell toCell = POIReadUtils.getCell(sheet, i, k);
                        toCell.setCellStyle(fromCell.getCellStyle());
                        toCell.setCellValue(fromCell.getStringCellValue());
                    }

                    for (int k = 0; k < grammars.get(value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), format("%s%s%s", ELEMENT_START.getCode(), INDEX_WILDCARD.getCode(), ELEMENT_END.getCode()))).size(); k++) {
                        XSSFCell tempCell = POIReadUtils.getCell(sheet, cell.getRowIndex(), cell.getColumnIndex() + k);
                        String newCellValue = StringUtils.replace(cellValue, value, value.replaceAll(format("%s%s%s", ELEMENT_START.getCodeAfterEscapeRegexCharacters(), HORIZONTAL_ARRAY.getCode(), ELEMENT_END.getCodeAfterEscapeRegexCharacters()), "[" + k + "]"), 1);
                        tempCell.setCellValue(newCellValue);
                    }
                }
            }
        }
    }

    private static void fillPlaceholders(XSSFSheet sheet, Map<String, String> grammarMap) {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) continue;

            for (int j = 0; j <= row.getLastCellNum(); j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null) continue;
                if (cell.getCellType() != STRING) continue;
                String cellValue;
                if ((cellValue = cell.getStringCellValue()).isEmpty()) continue;
                List<String> placeholders;
                if ((placeholders = ReUtil.findAllGroup0(PLACEHOLDER.getCode(), cellValue)).isEmpty()) continue;

                for (String placeholder : placeholders) {
                    String placeholderWithoutWrapper = placeholder.substring(PLACEHOLDER_START.getCode().length(), placeholder.length() - PLACEHOLDER_END.getCode().length());

                    String placeholderValue = grammarMap.entrySet().stream()
                            .filter(entry -> entry.getKey().equals(placeholderWithoutWrapper))
                            .findFirst()
                            .map(Map.Entry::getValue)
                            .orElse(EMPTY);

                    String newCellValue = StringUtils.replace(cell.getStringCellValue(), placeholder, placeholderValue, 1);
                    cell.setCellValue(newCellValue);
                }
            }
        }
    }

    /**
     * 模板sheet页与数据一对一导出
     *
     * @param mapping
     * @return
     */
    public byte[] exportByTemplate(SheetMapping mapping) {
        if (mapping == null) {
            throw new ExcelExportException("没有数据可供导出");
        }

        FieldBean bean;
        if ((bean = ValidatorUtils.validator(mapping)).isSuccess()) {
            throw new ExcelExportException("数据校验失败：" + bean.getMsg());
        }

        List<Property> properties = getProperties(mapping);
        Map<String, String> grammarMap = new HashMap<>();
        generateGrammars(properties, grammarMap);
        Map<String, List<String>> arrayGrammarMap = generateArrayGrammar(grammarMap);

        ClassPathResource resource = new ClassPathResource(this.templatePath);

        try (InputStream inputStream = resource.getInputStream()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                if (preHandle != null) {
                    preHandle.accept(workbook);
                }

                workbook.setSheetName(0, mapping.getSheetName());
                expandPlaceholders(workbook.getSheetAt(0), arrayGrammarMap);
                fillPlaceholders(workbook.getSheetAt(0), grammarMap);

                if (postHandle != null) {
                    postHandle.accept(workbook);
                }

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    try {
                        workbook.write(outputStream);
                    } catch (IOException e) {
                        throw new ExcelExportException("Excel导出失败");
                    }
                    return outputStream.toByteArray();
                } catch (IOException ignored) {
                    return new byte[0];
                }
            } catch (IOException e) {
                throw new ExcelExportException("模板加载失败");
            }
        } catch (IOException e) {
            throw new ExcelExportException("找不到模板文件");
        }
    }

    /**
     * 根据单个模板sheet页导出多个sheet页
     *
     * @param mappings
     * @return
     */
    public <T extends SheetMapping> byte[] exportByTemplate(List<T> mappings) {
        if (CollectionUtils.isEmpty(mappings)) {
            throw new ExcelExportException("没有数据可供导出");
        }

        FieldBean bean;
        if ((bean = ValidatorUtils.validator(mappings)).isSuccess()) {
            throw new ExcelExportException("数据校验失败：" + bean.getMsg());
        }

        ClassPathResource resource = new ClassPathResource(this.templatePath);

        try (InputStream inputStream = resource.getInputStream()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                if (preHandle != null) {
                    preHandle.accept(workbook);
                }

                List<Integer> sheetIndexes = new ArrayList<>();
                sheetIndexes.add(0);

                workbook.setSheetName(0, mappings.get(0).getSheetName());

                if (mappings.size() > 1) {
                    for (T mapping : mappings.subList(1, mappings.size())) {
                        sheetIndexes.add(copySheet(workbook, 0, mapping.getSheetName()));
                    }
                }

                Iterator<T> iterator = mappings.iterator();
                for (Integer sheetIndex : sheetIndexes) {
                    T mapping = iterator.next();

                    List<Property> properties = getProperties(mapping);
                    Map<String, String> grammarMap = new HashMap<>();
                    generateGrammars(properties, grammarMap);
                    Map<String, List<String>> arrayGrammars = generateArrayGrammar(grammarMap);

                    expandPlaceholders(workbook.getSheetAt(sheetIndex), arrayGrammars);
                    fillPlaceholders(workbook.getSheetAt(sheetIndex), grammarMap);
                }

                if (postHandle != null) {
                    postHandle.accept(workbook);
                }

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    try {
                        workbook.write(outputStream);
                    } catch (IOException e) {
                        throw new ExcelExportException("Excel导出失败");
                    }
                    return outputStream.toByteArray();
                } catch (IOException ignored) {
                    return new byte[0];
                }
            } catch (IOException e) {
                throw new ExcelExportException("模板加载失败");
            }
        } catch (IOException e) {
            throw new ExcelExportException("找不到模板文件");
        }
    }

    /**
     * 模板sheet页与数据N对N导出
     *
     * @param mappings
     * @return
     */
    public <T extends SheetMapping> byte[] exportByMultiTemplate(List<T> mappings) {
        if (CollectionUtils.isEmpty(mappings)) {
            throw new ExcelExportException("没有数据可供导出");
        }

        FieldBean bean;
        if ((bean = ValidatorUtils.validator(mappings)).isSuccess()) {
            throw new ExcelExportException("数据校验失败：" + bean.getMsg());
        }

        ClassPathResource resource = new ClassPathResource(this.templatePath);

        try (InputStream inputStream = resource.getInputStream()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                if (preHandle != null) {
                    preHandle.accept(workbook);
                }

                for (int i = 0; i < Math.min(workbook.getNumberOfSheets(), mappings.size()); i++) {
                    SheetMapping mapping = mappings.get(i);

                    List<Property> properties = getProperties(mapping);
                    Map<String, String> grammarMap = new HashMap<>();
                    generateGrammars(properties, grammarMap);
                    Map<String, List<String>> arrayGrammars = generateArrayGrammar(grammarMap);

                    workbook.setSheetName(i, mapping.getSheetName());
                    expandPlaceholders(workbook.getSheetAt(i), arrayGrammars);
                    fillPlaceholders(workbook.getSheetAt(i), grammarMap);
                }

                if (postHandle != null) {
                    postHandle.accept(workbook);
                }

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    try {
                        workbook.write(outputStream);
                    } catch (IOException e) {
                        throw new ExcelExportException("Excel导出失败");
                    }
                    return outputStream.toByteArray();
                } catch (IOException ignored) {
                    return new byte[0];
                }
            } catch (IOException e) {
                throw new ExcelExportException("模板加载失败");
            }
        } catch (IOException e) {
            throw new ExcelExportException("找不到模板文件");
        }
    }

    public static class ExcelExporterBuilder {

        private String templatePath;

        private Consumer<XSSFWorkbook> preHandle;

        private Consumer<XSSFWorkbook> postHandle;

        private ExcelExporterBuilder() {
        }

        public ExcelExporterBuilder templatePath(String templatePath) {
            this.templatePath = templatePath;
            return this;
        }

        public ExcelExporterBuilder preHandle(Consumer<XSSFWorkbook> preHandle) {
            this.preHandle = preHandle;
            return this;
        }

        public ExcelExporterBuilder postHandle(Consumer<XSSFWorkbook> postHandle) {
            this.postHandle = postHandle;
            return this;
        }

        public ExcelExporter build() {
            this.vssert();
            ExcelExporter exporter = new ExcelExporter();
            exporter.templatePath = this.templatePath;
            exporter.preHandle = this.preHandle;
            exporter.postHandle = this.postHandle;
            return exporter;
        }

        private void vssert() {
            if (StringUtils.isBlank(this.templatePath)) {
                throw new ExcelExportException("模板路径不能为空");
            }
        }
    }
}