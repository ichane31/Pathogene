# DLDiagnosis - Deep Learning Disease Classification

## Version [1.1.0]

DLDiagnosis is a mobile and web application for diseases classification using Deep Learning. The project is divided into three main components: a web application integrated with the backend, a mobile application, and a Python component for loading and using pre-trained models.

### Software Stack

- JHipster
- TensorFlow
- Flutter
- Spring Boot
- Spring Security
- Java
- Python
- RabbitMQ
- Dart
- JavaScript

### Compilation Requirements and Dependencies

Make sure you have the following installed:

- Java 17+ JVM or GraalVM
- Python 3.10
- Flutter

### Project Structure

1. *Web Application (Frontend & Backend Integration):*
   - Developed with JHipster, Vue.js, Spring Boot, and Spring Security.
   - Frontend source code: [web-application/src/main/webapp](web-application/src/main/webapp)
   - Backend source code: [web-application/src/main/java](web-application/src/main/java)

2. *Mobile Application:*
   - Developed with Flutter and Dart.
   - Mobile app source code: [mobile-application/lib](mobile-application/lib)

3. *Python Component:*
   - Used for loading pre-trained models in .h5 format.
   - Predictions are published on a RabbitMQ queue and retrieved at the backend.
   - Python source code: [python-component][(python-component](https://github.com/najiaokacha/DLDiagnosisClassification/tree/master))

### Compilation and Execution

1. *Web Application:*
   ```bash
   # Navigate to the web application directory
   cd web-application

   # Install dependencies
   npm install

   # Run the application
   ./mvnw
