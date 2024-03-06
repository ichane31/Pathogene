package emsi.iir4.pathogene.web.rest;

import emsi.iir4.pathogene.domain.*;
import emsi.iir4.pathogene.repository.*;
import emsi.iir4.pathogene.security.AuthoritiesConstants;
import emsi.iir4.pathogene.security.SecurityUtils;
import emsi.iir4.pathogene.service.MailService;
import emsi.iir4.pathogene.service.UserService;
import emsi.iir4.pathogene.service.dto.AdminUserDTO;
import emsi.iir4.pathogene.service.dto.PasswordChangeDTO;
import emsi.iir4.pathogene.web.rest.errors.BadRequestAlertException;
import emsi.iir4.pathogene.web.rest.errors.EmailAlreadyUsedException;
import emsi.iir4.pathogene.web.rest.errors.InvalidPasswordException;
import emsi.iir4.pathogene.web.rest.errors.LoginAlreadyUsedException;
import emsi.iir4.pathogene.web.rest.vm.KeyAndPasswordVM;
import emsi.iir4.pathogene.web.rest.vm.ManagedUserVM;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private static final String userNotFound = "User could not be found";
    private static final String detectionsRequest = "REST request to get a page of Detections";

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final PatientRepository patientRepository;
    private final SecretaireRepository secretaireRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;

    private final DetectionRepository detectionRepository;

    public final StadeRepository stadeRepository;

    private final MqController mqController;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        RendezVousRepository rendezVousRepository,
        PatientRepository patientRepository,
        SecretaireRepository secretaireRepository,
        MedecinRepository medecinRepository,
        DetectionRepository detectionRepository,
        StadeRepository stadeRepository,
        MqController mqController
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.rendezVousRepository = rendezVousRepository;
        this.patientRepository = patientRepository;
        this.secretaireRepository = secretaireRepository;
        this.medecinRepository = medecinRepository;
        this.detectionRepository = detectionRepository;
        this.stadeRepository = stadeRepository;
        this.mqController = mqController;
    }

    @PutMapping("/account")
    public void updateAccount(@RequestBody AdminUserDTO userDTO, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userLogin(userDTO);
        if (isPasswordLengthInvalid(passwordChangeDTO.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.updateUser(userDTO);
        userService.changePassword(passwordChangeDTO.getCurrentPassword(), passwordChangeDTO.getNewPassword());
    }

    private void userLogin(@RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (user.isEmpty()) {
            throw new AccountResourceException(userNotFound);
        }
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
        patientRepository.save(new Patient().user(user).nom(user.getLastName()).prenom(user.getFirstName()));
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (user.isEmpty()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        AdminUserDTO userDTO = userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException(userNotFound));

        setUserMedecin(userDTO);
        setUserSecretaire(userDTO);
        setUserPatient(userDTO);

        return userDTO;
    }

    private void setUserMedecin(AdminUserDTO userDTO) {
        if (userDTO.getAuthorities().contains(AuthoritiesConstants.MEDECIN)) {
            Optional<Medecin> medecinOptional = medecinRepository.findByUserId(userDTO.getId());

            medecinOptional.ifPresent(medecin -> {
                if (Objects.equals(userDTO.getId(), medecin.getUser().getId())) {
                    userDTO.setMedecin(medecin);
                }
            });
        }
    }

    private void setUserSecretaire(AdminUserDTO userDTO) {
        if (userDTO.getAuthorities().contains(AuthoritiesConstants.SECRETAIRE)) {
            Optional<Secretaire> secretaireOptional = secretaireRepository.findByUserId(userDTO.getId());

            secretaireOptional.ifPresent(secretaire -> {
                if (Objects.equals(userDTO.getId(), secretaire.getUser().getId())) {
                    userDTO.setSecretaire(secretaire);
                }
            });
        }
    }

    private void setUserPatient(AdminUserDTO userDTO) {
        if (userDTO.getAuthorities().contains(AuthoritiesConstants.PATIENT)) {
            Optional<Patient> patientOptional = patientRepository.findByUserId(userDTO.getId());

            patientOptional.ifPresent(patient -> {
                if (Objects.equals(userDTO.getId(), patient.getUser().getId())) {
                    userDTO.setPatient(patient);
                }
            });
        }
    }

    @GetMapping("/medecin/patients")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.MEDECIN + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Patient>> getPatientsByMedecinId(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Set<RendezVous> rendezVous = new HashSet<>();
        if ((medecinRepository.findByUserId(getAccount().getId())).isPresent()) {
            rendezVous = rendezVousRepository.findByMedecinUserId(getAccount().getId());
        }
        Set<Patient> patients = new HashSet<>();
        for (RendezVous rdv : rendezVous) {
            patients.add(rdv.getPatient());
        }
        Page<Patient> patientPage = new PageImpl<>(new ArrayList<>(patients), pageable, patients.size());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), patientPage);
        return ResponseEntity.ok().headers(headers).body(new ArrayList<>(patients));
    }

    @GetMapping("/patient/medecins")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.PATIENT + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Medecin>> getMedecins(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Set<RendezVous> rendezVous = new HashSet<>();
        Optional<Patient> patientOptional = patientRepository.findByUserId(getAccount().getId());

        if (patientOptional.isPresent()) {
            rendezVous = rendezVousRepository.findByPatientUserId(getAccount().getId());
        }
        Set<Medecin> medecins = new HashSet<>();
        for (RendezVous rdv : rendezVous) {
            medecins.add(rdv.getMedecin());
        }
        Page<Medecin> medecinPage = new PageImpl<>(new ArrayList<>(medecins), pageable, medecins.size());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), medecinPage);
        return ResponseEntity.ok().headers(headers).body(new ArrayList<>(medecins));
    }

    @GetMapping("/detections/patient")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.PATIENT + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Detection>> getAllDetectionsByPatient(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        List<Detection> detections = new ArrayList<>();
        Optional<Patient> patientOptional = patientRepository.findByUserId(getAccount().getId());

        if (patientOptional.isPresent()) {
            detections = detectionRepository.findAllByPatientUserId(getAccount().getId());
            log.debug(detectionsRequest);
        }
        Page<Detection> page = new PageImpl<>(new ArrayList<>(detections), pageable, detections.size());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/detections/bymedecin")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.MEDECIN + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Detection>> getAllDetectionsByMedecin(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        List<Detection> detections = new ArrayList<>();
        if ((medecinRepository.findByUserId(getAccount().getId())).isPresent()) {
            detections = detectionRepository.findAllByMedecinUserId(getAccount().getId());
            log.debug(detectionsRequest);
        }
        Page<Detection> page = new PageImpl<>(new ArrayList<>(detections), pageable, detections.size());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/detections/medecin")
    public ResponseEntity<Detection> createDetection(@Valid @RequestBody Detection detection) throws URISyntaxException {
        log.debug("REST request to save Detection : {}", detection);
        if (detection.getId() != null) {
            throw new BadRequestAlertException("A new detection cannot already have an ID", "detection", "idexists");
        }

        String oracle = mqController.send(detection.getPhoto(), detection.getMaladie().getNom());
        detection.setDescription(oracle);
        String stade = oracle.split("Confidence This Is ")[1];
        detection.setStade(stade);
        detection.setCode("DET-" + UUID.randomUUID());
        Optional<Medecin> medecinOptional = medecinRepository.findByUserId(getAccount().getId());

        if (medecinOptional.isPresent()) {
            detection.setMedecin(medecinOptional.get());
            log.debug(detectionsRequest);
        }

        Detection result = detectionRepository.save(detection);
        return ResponseEntity
            .created(new URI("/api/detections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("PathogeneApp", true, "detection", result.getId().toString()))
            .body(result);
    }

    @GetMapping("/maladie/patient")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.PATIENT + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Maladie>> getMaladieByPatient() {
        Optional<Patient> optionalPatient = patientRepository.findByUserId(getAccount().getId());
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();

            Optional<Stade> optionalStade = stadeRepository.findById(patient.getStade().getId());

            if (optionalStade.isPresent()) {
                Stade stade = optionalStade.get();

                Maladie maladie = stade.getMaladie();

                List<Maladie> maladies = Collections.singletonList(maladie);

                return ResponseEntity.ok().body(maladies);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/visite/medecin")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.MEDECIN + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Visite>> getVisitesByMedecin(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        List<RendezVous> rendezVous = new ArrayList<>();
        if ((medecinRepository.findByUserId(getAccount().getId())).isPresent()) {
            rendezVous = rendezVousRepository.findAllByMedecinUserId(getAccount().getId());
        }
        Set<Visite> visites = new HashSet<>();
        for (RendezVous rdv : rendezVous) {
            Visite visite = rdv.getVisite();

            if (visite != null && visite.getRendezVous() == rdv) {
                visites.add(visite);
            }
        }
        Page<Visite> medecinPage = new PageImpl<>(new ArrayList<>(visites), pageable, visites.size());
        HttpHeaders headers;
        headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), medecinPage);
        return ResponseEntity.ok().headers(headers).body(new ArrayList<>(visites));
    }

    @GetMapping("/visite/patient")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.PATIENT + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Visite>> getVisitesByPatient(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        List<RendezVous> rendezVous = new ArrayList<>();
        if ((patientRepository.findByUserId(getAccount().getId())).isPresent()) {
            rendezVous = rendezVousRepository.findAllByPatientUserId(getAccount().getId());
        }
        Set<Visite> visites = new HashSet<>();
        for (RendezVous rdv : rendezVous) {
            Visite visite = rdv.getVisite();

            if (visite != null && visite.getRendezVous() == rdv) {
                visites.add(visite);
            }
        }
        Page<Visite> medecinPage = new PageImpl<>(new ArrayList<>(visites), pageable, visites.size());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), medecinPage);
        return ResponseEntity.ok().headers(headers).body(new ArrayList<>(visites));
    }

    @GetMapping("/rendez-vous/medecin")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.MEDECIN + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecin() {
        List<RendezVous> rendezVous = new ArrayList<>();
        if ((medecinRepository.findByUserId(getAccount().getId())).isPresent()) {
            rendezVous = rendezVousRepository.findAllByMedecinUserId(getAccount().getId());
        }

        return ResponseEntity.ok().body(rendezVous);
    }

    @GetMapping("/rendez-vous/secretaire")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.SECRETAIRE + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<RendezVous>> getRendezVousBySecretaire() {
        List<RendezVous> rendezVous;
        List<RendezVous> rendezVousSecretaire = new ArrayList<>();
        Optional<Secretaire> secretaireOptional = secretaireRepository.findByUserId(getAccount().getId());

        if (secretaireOptional.isPresent()) {
            rendezVous = rendezVousRepository.findAll();
            for (RendezVous rdv : rendezVous) {
                if (Objects.equals(rdv.getMedecin().getSecretaire().getId(), secretaireOptional.get().getId())) {
                    rendezVousSecretaire.add(rdv);
                }
            }
        }
        return ResponseEntity.ok().body(rendezVousSecretaire);
    }

    @GetMapping("/rendez-vous/patient")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.PATIENT + "','" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatient() {
        List<RendezVous> rendezVous = new ArrayList<>();
        if ((patientRepository.findByUserId(getAccount().getId())).isPresent()) {
            rendezVous = rendezVousRepository.findAllByPatientUserId(getAccount().getId());
        }

        return ResponseEntity.ok().body(rendezVous);
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        userLogin(userDTO);
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (user.isEmpty()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
