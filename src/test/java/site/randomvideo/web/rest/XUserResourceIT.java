package site.randomvideo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.randomvideo.IntegrationTest;
import site.randomvideo.domain.XUser;
import site.randomvideo.repository.XUserRepository;

/**
 * Integration tests for the {@link XUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class XUserResourceIT {

    private static final String ENTITY_API_URL = "/api/x-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private XUserRepository xUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restXUserMockMvc;

    private XUser xUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static XUser createEntity(EntityManager em) {
        XUser xUser = new XUser();
        return xUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static XUser createUpdatedEntity(EntityManager em) {
        XUser xUser = new XUser();
        return xUser;
    }

    @BeforeEach
    public void initTest() {
        xUser = createEntity(em);
    }

    @Test
    @Transactional
    void createXUser() throws Exception {
        int databaseSizeBeforeCreate = xUserRepository.findAll().size();
        // Create the XUser
        restXUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(xUser)))
            .andExpect(status().isCreated());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeCreate + 1);
        XUser testXUser = xUserList.get(xUserList.size() - 1);
    }

    @Test
    @Transactional
    void createXUserWithExistingId() throws Exception {
        // Create the XUser with an existing ID
        xUser.setId(1L);

        int databaseSizeBeforeCreate = xUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restXUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(xUser)))
            .andExpect(status().isBadRequest());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllXUsers() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        // Get all the xUserList
        restXUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(xUser.getId().intValue())));
    }

    @Test
    @Transactional
    void getXUser() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        // Get the xUser
        restXUserMockMvc
            .perform(get(ENTITY_API_URL_ID, xUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(xUser.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingXUser() throws Exception {
        // Get the xUser
        restXUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingXUser() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();

        // Update the xUser
        XUser updatedXUser = xUserRepository.findById(xUser.getId()).get();
        // Disconnect from session so that the updates on updatedXUser are not directly saved in db
        em.detach(updatedXUser);

        restXUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedXUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedXUser))
            )
            .andExpect(status().isOk());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
        XUser testXUser = xUserList.get(xUserList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, xUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(xUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(xUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(xUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateXUserWithPatch() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();

        // Update the xUser using partial update
        XUser partialUpdatedXUser = new XUser();
        partialUpdatedXUser.setId(xUser.getId());

        restXUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedXUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedXUser))
            )
            .andExpect(status().isOk());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
        XUser testXUser = xUserList.get(xUserList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateXUserWithPatch() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();

        // Update the xUser using partial update
        XUser partialUpdatedXUser = new XUser();
        partialUpdatedXUser.setId(xUser.getId());

        restXUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedXUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedXUser))
            )
            .andExpect(status().isOk());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
        XUser testXUser = xUserList.get(xUserList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, xUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(xUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(xUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamXUser() throws Exception {
        int databaseSizeBeforeUpdate = xUserRepository.findAll().size();
        xUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restXUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(xUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the XUser in the database
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteXUser() throws Exception {
        // Initialize the database
        xUserRepository.saveAndFlush(xUser);

        int databaseSizeBeforeDelete = xUserRepository.findAll().size();

        // Delete the xUser
        restXUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, xUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<XUser> xUserList = xUserRepository.findAll();
        assertThat(xUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
