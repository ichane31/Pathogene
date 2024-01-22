package emsi.iir4.pathogene.web.rest;

import emsi.iir4.pathogene.service.FirebaseFileService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/models/upload")
public class ImageUploadController {

    @Autowired
    private final FirebaseFileService firebaseFileService;

    public ImageUploadController(FirebaseFileService firebaseFileService) {
        this.firebaseFileService = firebaseFileService;
    }

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("maladieName") String maladieName) {
        return this.firebaseFileService.uploadModelFile(multipartFile, maladieName.toLowerCase());
    }

    @DeleteMapping
    public String delete(@RequestParam("filename") String filename) {
        try {
            return this.firebaseFileService.delete(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
