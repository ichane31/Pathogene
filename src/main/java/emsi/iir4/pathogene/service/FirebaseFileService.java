package emsi.iir4.pathogene.service;

import static org.springframework.util.StringUtils.cleanPath;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseFileService {

    @Value("${fileProd.storage.path}")
    private String DOWNLOAD_URL;

    private String BUCKET_NAME = "pathogene-258d1.appspot.com";

    private final Storage storage;
    private final Credentials credentials;

    public FirebaseFileService() throws IOException {
        this.credentials =
            GoogleCredentials.fromStream(
                new FileInputStream("src/main/resources/pathogene-258d1-firebase-adminsdk-8yl0t-1537919e0a_java.json")
            );
        this.storage = StorageOptions.newBuilder().setCredentials(this.credentials).build().getService();
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String generateFileName(MultipartFile file, String maladieName) {
        String originalFileName = cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // Utiliser le nom de la maladie et un UUID pour générer le nouveau nom du fichier
        return maladieName + "_" + UUID.randomUUID() + fileExtension;
    }

    public String uploadModelFile(MultipartFile multipartFile, String maladieName) {
        try {
            String fileName = generateFileName(multipartFile, maladieName);
            File file = convertToFile(multipartFile, fileName);
            String URL = uploadFile(file, fileName);
            file.delete();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

    public String delete(String fileName) throws IOException {
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, fileName));
        if (blob != null) {
            storage.delete(BlobId.of(BUCKET_NAME, fileName));
            return "Successfully Deleted!";
        } else {
            return "File do not exist!";
        }
    }
}
