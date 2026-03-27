package com.yy.ppm.equipment.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yy.ppm.equipment.bean.dto.QrCodeDataDTO;

import java.io.OutputStream;
import java.util.List;

/**
 * 生成PDF二维码文件的工具类
 * @author system
 */
public class PdfFileUtil {

    /**
     * 生成并输出二维码PDF
     * @param qrCodeDataList 二维码集合
     * @param outputStream 输出流
     */
    public static void generateQrCodePdf(List<QrCodeDataDTO> qrCodeDataList, OutputStream outputStream) throws Exception {
        // A4纸张默认大小是 595 x 842 磅
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        
        // 设置页边距 (上下左右各30磅)
        document.setMargins(30, 30, 30, 30);
        document.open();

        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont, 10, Font.NORMAL);

        // 每页3列4行，共12个
        int cols = 3;
        int rowsPerPage = 4;
        int itemsPerPage = cols * rowsPerPage;

        for (int i = 0; i < qrCodeDataList.size(); i += itemsPerPage) {
            if (i > 0) {
                document.newPage();
            }

            PdfPTable mainTable = new PdfPTable(cols);
            mainTable.setWidthPercentage(100);
            
            for (int j = 0; j < itemsPerPage; j++) {
                int index = i + j;
                if (index < qrCodeDataList.size()) {
                    QrCodeDataDTO data = qrCodeDataList.get(index);
                    PdfPCell cell = createCell(font, data.getBytes(), data.getCodeName());
                    // 如果需要边框可以开启，或者保持 Rectangle.NO_BORDER
                    cell.setBorder(Rectangle.BOX); 
                    cell.setBorderColor(BaseColor.LIGHT_GRAY);
                    mainTable.addCell(cell);
                } else {
                    // 填充空白单元格
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(Rectangle.NO_BORDER);
                    mainTable.addCell(emptyCell);
                }
            }
            document.add(mainTable);
        }
        
        document.close();
    }

    /**
     * 创建存放二维码和标题的单元格
     */
    private static PdfPCell createCell(Font font, byte[] qrCodeImage, String title) throws Exception {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // 内部表格用于垂直排列
        PdfPTable nestedTable = new PdfPTable(1);
        nestedTable.setWidthPercentage(100);

        // 二维码图片
        Image image = Image.getInstance(qrCodeImage);
        // 关键：强制设置图片在PDF中的显示尺寸 (单位是磅，1mm ≈ 2.83磅)
        // 设置为约 45mm x 45mm
        float sideInPoints = 45 * 2.83f;
        image.scaleAbsolute(sideInPoints, sideInPoints);
        image.setAlignment(Image.ALIGN_CENTER);
        
        PdfPCell imageCell = new PdfPCell(image);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell.setPaddingBottom(2f);
        nestedTable.addCell(imageCell);
        
        // 文字内容
        Paragraph p = new Paragraph(title, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setLeading(10f); // 行间距

        PdfPCell textCell = new PdfPCell();
        textCell.addElement(p);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        textCell.setVerticalAlignment(Element.ALIGN_TOP);
        textCell.setPaddingTop(2f);
        textCell.setMinimumHeight(35f); // 适当压缩高度
        nestedTable.addCell(textCell);
        
        cell.addElement(nestedTable);
        return cell;
    }
}
