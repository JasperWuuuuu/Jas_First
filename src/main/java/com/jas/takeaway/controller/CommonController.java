package com.jas.takeaway.controller;

import com.jas.takeaway.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上傳和下載
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${take_away.path}")
    private String basePath;
    /**
     * 文件上傳
     * @param file  //形參名一定要和頁面發送的一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一個臨時文件，需要轉存到指定位置，否則本次請求完成後臨時文件就會刪除
        log.info(file.toString());  //getClass+getName+@+HashCode

        //原始文件名
        String originalFilename = file.getOriginalFilename();  // abc.jpg
        String suffix = originalFilename
                .substring(originalFilename.lastIndexOf("."));  // .jpg

        //使用UUID重新生成文件名，防止文件名重複造成文件覆蓋
        String fileName = UUID.randomUUID().toString()+suffix; // xyz.jpg

        //創建目錄對象
        File dir = new File(basePath);
        //判斷目錄是否存在，不存在就需要創建
        if (!dir.exists()){
            dir.mkdirs();
        }

        try {
            //臨時文件轉存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下載
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //輸入流 -- 讀取文件內容
            FileInputStream fileInputStream
                    = new FileInputStream(new File(basePath+name));
            //輸出流 -- 文件寫回瀏覽器去展示圖片
            ServletOutputStream outputStream = response.getOutputStream();

            //設置響應圖片
            response.setContentType("image/jpeg");

            //每次讀取然後把數據放入到bytes數組中
            int length = 0;
            byte[] bytes = new byte[1024];
            while((length=fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
