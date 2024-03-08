package emsi.iir4.pathogene.web.rest;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mq")
public class MqController {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;

    @PostMapping("/analyze")
    public String send(@RequestParam byte[] photo, @RequestParam String name) {
        // BigInteger.valueOf(id).toByteArray()
        System.out.println(" [x] Requesting classification.....");
        System.out.println("from :" + exchange.getName());
        System.out.println("name :" + name);
        byte[] response;
        switch (name) {
            case "brain Cancer":
                response = (byte[]) template.convertSendAndReceive("", "rpc_brain", photo);
                break;
            case "Rethinopathy":
                response = (byte[]) template.convertSendAndReceive("", "rpc_retino", photo);
                break;
            case "Pneumonia":
                response = (byte[]) template.convertSendAndReceive("", "rpc_pneumonia", photo);
                break;
            case "Breast Cancer":
                response = (byte[]) template.convertSendAndReceive("", "rpc_breast", photo);
                break;
            case "Keratocone":
                response = (byte[]) template.convertSendAndReceive("", "rpc_keratocone", photo);
                break;
            default:
                // Handle unknown disease name
                return "Unknown disease: " + name;
        }
        String oracle;
        if (response != null) oracle = new String(response); else oracle = new String(new byte[0]);
        System.out.println("[x]" + oracle);
        return oracle;
    }
    /*
    Dynamique
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
            maladieInfo.setNom(maladie.getNom().toLowerCase());
            maladieInfo.setModeleFileName(maladie.getModeleFileName());
            maladieInfo.setNormalizationValue(maladie.getNormalizationValue());
            // Add other relevant information
            // Convert the DTO to JSON and send it along with the photo
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String maladieInfoJson = objectMapper.writeValueAsString(maladieInfo);
                byte[] jsonBytes = maladieInfoJson.getBytes(StandardCharsets.UTF_8);

                // Concatenate JSON bytes and photo bytes
                byte[] message = ArrayUtils.addAll(jsonBytes, photo);

                byte[] response = (byte[]) template.convertSendAndReceive("", "rpc_" + maladieName.toLowerCase(), message);
                String oracle = (response != null) ? new String(response, StandardCharsets.UTF_8) : null;
                System.out.println("[x]" + oracle);
                String[] parts = oracle != null ? oracle.split("\\s+") : null;
                if (parts.length >= 5 && "Confidence".equals(parts[1]) && "This".equals(parts[2]) && "Is".equals(parts[3])) {
                    // Extract the class number from the response
                    try {
                        int classNumber = Integer.parseInt(parts[4]);
                        // Get the corresponding class name from the classNamesMapping
                        String className = maladie.getClassNamesMapping().get(classNumber);
                        // Replace the class number with the class name in the response
                        oracle = oracle != null ? oracle.replace(parts[4], className) : null;
                    } catch (NumberFormatException e) {
                        // Handle the case where the class number is not a valid integer
                        log.error(e.getMessage());
                    }
                }
                return oracle;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                return "Error processing JSON" + e.getMessage();
            }
        } else {
            return "Model not available for disease: " + maladieName;
        }
    }
     */

    @PostMapping("/ping")
    public String ping() {
        byte[] response = (byte[]) template.convertSendAndReceive("", "rpc_queue", "");
        return new String(response);
    }
}
