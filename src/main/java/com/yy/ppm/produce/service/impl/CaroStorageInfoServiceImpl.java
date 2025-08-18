package com.yy.ppm.produce.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.service.CaroStorageInfoService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CaroStorageInfoServiceImpl implements CaroStorageInfoService {
    private static final int QR_CODE_SIZE = 200;

    @Override
    public String createQRcode(String infoString,String pathHeader) {

        if(pathHeader==null||pathHeader.length()<=0||"".equals(pathHeader)){
            pathHeader = "C:\\QRCodeFIle\\";
        }
        String pathString = pathHeader+String.valueOf(System.currentTimeMillis())+".PNG";
        File file = new File(pathString);
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }
        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(infoString, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            Path path = Paths.get(pathString);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        }catch (Exception e){
            throw new BusinessRuntimeException(e.getMessage());
        }
        return pathString;
    }
}
