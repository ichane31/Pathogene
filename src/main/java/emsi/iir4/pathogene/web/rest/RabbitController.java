package emsi.iir4.pathogene.web.rest;

import emsi.iir4.pathogene.domain.Maladie;
import emsi.iir4.pathogene.repository.MaladieRepository;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void initializeModels() {
        // Chargez la liste des noms de toutes les maladies et leurs modèles associés
        models = maladieRepository.findAll().stream().collect(Collectors.toMap(Maladie::getNom, Maladie::getModeleFileName));
    }

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
}
