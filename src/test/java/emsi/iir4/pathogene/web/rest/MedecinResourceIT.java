package emsi.iir4.pathogene.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import emsi.iir4.pathogene.IntegrationTest;
import emsi.iir4.pathogene.domain.Medecin;
import emsi.iir4.pathogene.repository.MedecinRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link MedecinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MedecinResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_EMP = "AAAAAAAAAA";
    private static final String UPDATED_NUM_EMP = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final Integer DEFAULT_EXPERT_LEVEL = 1;
    private static final Integer UPDATED_EXPERT_LEVEL = 2;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NBR_PATIENTS = 1;
    private static final Integer UPDATED_NBR_PATIENTS = 2;

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/medecins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMedecinMockMvc;

    private Medecin medecin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createEntity(EntityManager em) {
        Medecin medecin = new Medecin()
            .code(DEFAULT_CODE)
            .nom(DEFAULT_NOM)
            .numEmp(DEFAULT_NUM_EMP)
            .prenom(DEFAULT_PRENOM)
            .expertLevel(DEFAULT_EXPERT_LEVEL)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .type(DEFAULT_TYPE)
            .nbrPatients(DEFAULT_NBR_PATIENTS)
            .rating(DEFAULT_RATING)
            .description(DEFAULT_DESCRIPTION);
        return medecin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createUpdatedEntity(EntityManager em) {
        Medecin medecin = new Medecin()
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .numEmp(UPDATED_NUM_EMP)
            .prenom(UPDATED_PRENOM)
            .expertLevel(UPDATED_EXPERT_LEVEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE)
            .nbrPatients(UPDATED_NBR_PATIENTS)
            .rating(UPDATED_RATING)
            .description(UPDATED_DESCRIPTION);
        return medecin;
    }

    @BeforeEach
    public void initTest() {
        medecin = createEntity(em);
    }

    @Test
    @Transactional
    void createMedecin() throws Exception {
        int databaseSizeBeforeCreate = medecinRepository.findAll().size();
        // Create the Medecin
        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isCreated());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeCreate + 1);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testMedecin.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testMedecin.getNumEmp()).isEqualTo(DEFAULT_NUM_EMP);
        assertThat(testMedecin.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testMedecin.getExpertLevel()).isEqualTo(DEFAULT_EXPERT_LEVEL);
        assertThat(testMedecin.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testMedecin.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testMedecin.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMedecin.getNbrPatients()).isEqualTo(DEFAULT_NBR_PATIENTS);
        assertThat(testMedecin.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testMedecin.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createMedecinWithExistingId() throws Exception {
        // Create the Medecin with an existing ID
        medecin.setId(1L);

        int databaseSizeBeforeCreate = medecinRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMedecins() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        // Get all the medecinList
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].numEmp").value(hasItem(DEFAULT_NUM_EMP)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].expertLevel").value(hasItem(DEFAULT_EXPERT_LEVEL)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].nbrPatients").value(hasItem(DEFAULT_NBR_PATIENTS)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        // Get the medecin
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL_ID, medecin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(medecin.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.numEmp").value(DEFAULT_NUM_EMP))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.expertLevel").value(DEFAULT_EXPERT_LEVEL))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.nbrPatients").value(DEFAULT_NBR_PATIENTS))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMedecin() throws Exception {
        // Get the medecin
        restMedecinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();

        // Update the medecin
        Medecin updatedMedecin = medecinRepository.findById(medecin.getId()).get();
        // Disconnect from session so that the updates on updatedMedecin are not directly saved in db
        em.detach(updatedMedecin);
        updatedMedecin
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .numEmp(UPDATED_NUM_EMP)
            .prenom(UPDATED_PRENOM)
            .expertLevel(UPDATED_EXPERT_LEVEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE)
            .nbrPatients(UPDATED_NBR_PATIENTS)
            .rating(UPDATED_RATING)
            .description(UPDATED_DESCRIPTION);

        restMedecinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMedecin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testMedecin.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMedecin.getNumEmp()).isEqualTo(UPDATED_NUM_EMP);
        assertThat(testMedecin.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testMedecin.getExpertLevel()).isEqualTo(UPDATED_EXPERT_LEVEL);
        assertThat(testMedecin.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testMedecin.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testMedecin.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMedecin.getNbrPatients()).isEqualTo(UPDATED_NBR_PATIENTS);
        assertThat(testMedecin.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testMedecin.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, medecin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin.nom(UPDATED_NOM).numEmp(UPDATED_NUM_EMP).expertLevel(UPDATED_EXPERT_LEVEL).nbrPatients(UPDATED_NBR_PATIENTS);

        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testMedecin.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMedecin.getNumEmp()).isEqualTo(UPDATED_NUM_EMP);
        assertThat(testMedecin.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testMedecin.getExpertLevel()).isEqualTo(UPDATED_EXPERT_LEVEL);
        assertThat(testMedecin.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testMedecin.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testMedecin.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMedecin.getNbrPatients()).isEqualTo(UPDATED_NBR_PATIENTS);
        assertThat(testMedecin.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testMedecin.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .numEmp(UPDATED_NUM_EMP)
            .prenom(UPDATED_PRENOM)
            .expertLevel(UPDATED_EXPERT_LEVEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE)
            .nbrPatients(UPDATED_NBR_PATIENTS)
            .rating(UPDATED_RATING)
            .description(UPDATED_DESCRIPTION);

        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testMedecin.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMedecin.getNumEmp()).isEqualTo(UPDATED_NUM_EMP);
        assertThat(testMedecin.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testMedecin.getExpertLevel()).isEqualTo(UPDATED_EXPERT_LEVEL);
        assertThat(testMedecin.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testMedecin.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testMedecin.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMedecin.getNbrPatients()).isEqualTo(UPDATED_NBR_PATIENTS);
        assertThat(testMedecin.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testMedecin.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, medecin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();
        medecin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        int databaseSizeBeforeDelete = medecinRepository.findAll().size();

        // Delete the medecin
        restMedecinMockMvc
            .perform(delete(ENTITY_API_URL_ID, medecin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
