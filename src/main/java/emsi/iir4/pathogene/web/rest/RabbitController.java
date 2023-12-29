package emsi.iir4.pathogene.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import emsi.iir4.pathogene.domain.Maladie;
import emsi.iir4.pathogene.repository.MaladieRepository;
import emsi.iir4.pathogene.service.dto.MaladieInfoDTO;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mqrabbit")
public class RabbitController {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;

    @Autowired
    private MaladieRepository maladieRepository;

    // Map contenant les noms de toutes les maladies et leurs modèles associés
    private Map<String, String> models;

    //@PostConstruct
    public void initializeModels() {
        // Chargez la liste des noms de toutes les maladies et leurs modèles associés
        models =
            maladieRepository
                .findAll()
                .stream()
                .filter(maladie -> maladie.getNom() != null && maladie.getModeleFileName() != null)
                .collect(Collectors.toMap(Maladie::getNom, Maladie::getModeleFileName));
    }

    /*
    @PostMapping("/analyze")
    public String send(@RequestParam byte[] photo, @RequestParam String maladieName) {
        // Validation des paramètres
        if (photo == null || photo.length == 0 || maladieName == null || maladieName.isEmpty()) {
            return "Invalid parameters";
        }

        System.out.println(" [x] Requesting classification.....");
        System.out.println("from :" + exchange.getName());

        // Charger dynamiquement le modèle en fonction du nom de la maladie
        String modeleFileName = models.get(maladieName);

        if (modeleFileName != null && !modeleFileName.isEmpty()) {
            byte[] response = (byte[]) template.convertSendAndReceive("", "rpc_" + maladieName.toLowerCase(), photo);
            String oracle = (response != null) ? new String(response) : null;
            System.out.println("[x]" + oracle);
            return oracle;
        } else {
            return "Model not available for disease: " + maladieName;
        }
    }
     */

    @PostMapping("/analyze")
    public String send(@RequestParam byte[] photo, @RequestParam String maladieName) {
        // Validation des paramètres
        if (photo == null || photo.length == 0 || maladieName == null || maladieName.isEmpty()) {
            return "Invalid parameters";
        }

        System.out.println(" [x] Requesting classification for disease: " + maladieName);

        // Retrieve information about the disease
        Maladie maladie = maladieRepository.findByNom(maladieName);

        if (maladie.getModeleFileName() != null && !maladie.getModeleFileName().isEmpty()) {
            // Create a DTO (Data Transfer Object) to hold information about the disease
            MaladieInfoDTO maladieInfo = new MaladieInfoDTO();
            maladieInfo.setHeight(maladie.getHeight());
            maladieInfo.setWidth(maladie.getWidth());
            maladieInfo.setNom(maladie.getNom());
            maladieInfo.setNormalizationValue(maladie.getNormalizationValue());
            // Add other relevant information

            // Convert the DTO to JSON and send it along with the photo
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String maladieInfoJson = objectMapper.writeValueAsString(maladieInfo);
                byte[] message = ArrayUtils.addAll(maladieInfoJson.getBytes(), photo);

                byte[] response = (byte[]) template.convertSendAndReceive("", "rpc_" + maladieName.toLowerCase(), message);
                String oracle = (response != null) ? new String(response) : null;
                System.out.println("[x]" + oracle);
                return oracle;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "Error processing JSON";
            }
        } else {
            return "Model not available for disease: " + maladieName;
        }
    }
}
