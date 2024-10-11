package com.xuecheng.media;

import com.xuecheng.media.exception.MediaException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BigFileChunkTest {

    public static final int CHUNK_SIZE = 1024 * 1024 * 5;
    public static final String DIST_FILE = "D:\\document\\baiduNet\\test\\";
    public static final String SOURCE_FILE = "D:\\document\\baiduNet\\7月27日.mp4";

    @SneakyThrows
    @Test
    public void testChunk() {
        //源文件
        File sourceFile = new File("");
        //块文件路径
        String chunkPath = "";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize = 1024 * 1024 * 1;
        //计算块数，向上取整
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //缓冲区大小
        byte[] buffer = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //遍历分块，依次向每一个分块写入数据
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件，默认文件名 path + i，例如chunk\1  chunk\2
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                int len;
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                //向分块文件写入数据
                while ((len = raf_read.read(buffer)) != -1) {
                    raf_read.read(buffer, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
        System.out.println("写入分块完毕");
    }

    @Test
    public void testChunkV2() {
        //分块文件序号
        int count = 0;
        //统计已读取的文件长度
        long bytesRead = 0;
        //读取源文件
        File sourceFile = new File(SOURCE_FILE);
        //源文件总长度
        long totalLength = sourceFile.length();
        try (RandomAccessFile inputStream = new RandomAccessFile(sourceFile, "r")) {
            //遍历
            while (totalLength - bytesRead > 0) {
                File file = new File(DIST_FILE + count++);
                byte[] buffer = new byte[CHUNK_SIZE];
                if (file.createNewFile()) {
                    int len = (int) Math.min(totalLength - bytesRead, buffer.length);
                    inputStream.read(buffer, 0, len);
                    try (RandomAccessFile outputStream = new RandomAccessFile(file, "rw")) {
                        outputStream.write(buffer, 0, len);
                    } catch (IOException e) {
                        log.error("数据写入出错！", e);
                        MediaException.cast("数据写入出错");
                    }
                    if (len < buffer.length) {
                        break;
                    }
                    bytesRead += len;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @SneakyThrows
    public void testMerge() {
        //块文件目录
        File chunkFolder = new File(DIST_FILE);
        //源文件
        File sourceFile = new File(SOURCE_FILE);
        //合并文件
        File mergeFile = new File(DIST_FILE+"merge.mp4");

        //缓冲区
        byte[] buffer = new byte[1024];
        //文件名升序排序
        File[] files = chunkFolder.listFiles();
        // 文件名升序排序
        List<File> fileList = Arrays.stream(chunkFolder.listFiles())
                .sorted((f1, f2) -> {
                    // 假设文件名是数字，进行自然排序
                    return Integer.compare(Integer.parseInt(f1.getName()), Integer.parseInt(f2.getName()));
                })
                .collect(Collectors.toList());
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len;
            while ((len = raf_read.read(buffer)) != -1) {
                raf_write.write(buffer,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
        // 判断合并后的文件是否与源文件相同
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        //取出原始文件的md5
        String originalMd5 = DigestUtils.md5Hex(fileInputStream);
        //取出合并文件的md5进行比较
        String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
        if (originalMd5.equals(mergeFileMd5)) {
            System.out.println("合并文件成功");
        } else {
            System.out.println("合并文件失败");
        }
    }
}
