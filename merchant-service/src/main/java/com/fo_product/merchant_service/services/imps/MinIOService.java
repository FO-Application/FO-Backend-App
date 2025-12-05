package com.fo_product.merchant_service.services.imps;

import com.fo_product.merchant_service.exceptions.MinIOException;
import com.fo_product.merchant_service.exceptions.codes.MinIOExceptionCode;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinIOService implements IMinIOService {
    MinioClient minioClient;

    @NonFinal
    @Value("${minio.bucket-name}")
    String bucketName;

    // Khởi tạo Bucket nếu chưa tồn tại
    @PostConstruct
    private void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // Set Policy Public để xem được ảnh từ trình duyệt
                String policy = """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": {"AWS": ["*"]},
                              "Action": ["s3:GetObject"],
                              "Resource": ["arn:aws:s3:::%s/*"]
                            }
                          ]
                        }
                        """.formatted(bucketName);

                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());

                log.info("Bucket '{}' created successfully with public policy.", bucketName);
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage());
            throw new MinIOException(MinIOExceptionCode.INITIALIZING_BUCKET_FAILED);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            //Lấy tên file(Bao gồm cả đuôi file)
            String originalFileName = file.getOriginalFilename();
            //Chỉ lấy đuôi file nếu tìm thấy dấu chấm VD abc.jpg thì dấu chấm nằm ở vị trí index 3 => 3 != 1 --> Dùng để tránh lỗi index out of bounds!
            String extension = (originalFileName != null && originalFileName.lastIndexOf(".") != -1)
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";

            String newFileName = UUID.randomUUID().toString() + extension;
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newFileName)
                            /*
                            "Tôi đưa cho bạn luồng dữ liệu này (inputStream),
                            tổng dung lượng của nó là bao nhiêu đây (file.getSize()).
                            Còn việc chia nhỏ file ra để upload thế nào cho tối ưu thì bạn tự tính nhé (-1)."
                             */
                            .stream(inputStream, file.getSize(), -1) //-1 là part size(Để tự động)
                            .contentType(file.getContentType())
                            .build()
            );
            return getPublicUrl(newFileName);
        } catch (Exception e) {
            throw new MinIOException(MinIOExceptionCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
        );
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new MinIOException(MinIOExceptionCode.DELETE_FILE_FAILED);
        }
    }

    @Override
    public String getPublicUrl(String fileName) {
        //Nơi đổi và viết domain thật cho front end dùng link để hiển thị hình ảnh
        // VD: http://localhost:9000/bucket-name/ten-file.jpg
        return "http://localhost:8080/" + bucketName + "/" + fileName;
    }
}
