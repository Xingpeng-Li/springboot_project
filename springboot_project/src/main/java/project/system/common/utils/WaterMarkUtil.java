package project.system.common.utils;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
 @author:李星鹏
 @createDate:2020/11/3
 @description:给word文件添加水印功能类
 */
public class WaterMarkUtil {
    //给word(docx)添加水印
    public static File addWatermarkToWord(MultipartFile file, String watermark) throws IOException {
        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        addWaterMark(doc,watermark);
        File returnFile = new File("temp");
        OutputStream os = new FileOutputStream(returnFile);
        doc.write(os);
        os.close();
        doc.close();
        return returnFile;
    }
    private static void addWaterMark(XWPFDocument doc, String watermark) {
        //给文件添加水印
        XWPFHeaderFooterPolicy headerFooterPolicy = doc.createHeaderFooterPolicy();
        headerFooterPolicy.createWatermark(watermark);
        XWPFHeader header = headerFooterPolicy.getHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = header.getParagraphArray(0);
        org.apache.xmlbeans.XmlObject[] xmlobjects = paragraph.getCTP().getRArray(0).getPictArray(0).selectChildren(
                new javax.xml.namespace.QName("urn:schemas-microsoft-com:vml", "shape"));
        if (xmlobjects.length > 0) {
            com.microsoft.schemas.vml.CTShape ctshape = (com.microsoft.schemas.vml.CTShape) xmlobjects[0];
            ctshape.setFillcolor("#d8d8d8");
            ctshape.setStyle(ctshape.getStyle() + ";rotation:315");
        }
    }
}