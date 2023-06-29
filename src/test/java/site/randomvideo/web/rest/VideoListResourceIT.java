package site.randomvideo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.randomvideo.IntegrationTest;
import site.randomvideo.domain.VideoList;
import site.randomvideo.repository.VideoListRepository;

/**
 * Integration tests for the {@link VideoListResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VideoListResourceIT {

    private static final String DEFAULT_VIDEO_LIST_URL_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_LIST_URL_SLUG = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/video-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VideoListRepository videoListRepository;

    @Mock
    private VideoListRepository videoListRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVideoListMockMvc;

    private VideoList videoList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VideoList createEntity(EntityManager em) {
        VideoList videoList = new VideoList().videoListUrlSlug(DEFAULT_VIDEO_LIST_URL_SLUG);
        return videoList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VideoList createUpdatedEntity(EntityManager em) {
        VideoList videoList = new VideoList().videoListUrlSlug(UPDATED_VIDEO_LIST_URL_SLUG);
        return videoList;
    }

    @BeforeEach
    public void initTest() {
        videoList = createEntity(em);
    }

    @Test
    @Transactional
    void createVideoList() throws Exception {
        int databaseSizeBeforeCreate = videoListRepository.findAll().size();
        // Create the VideoList
        restVideoListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(videoList)))
            .andExpect(status().isCreated());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeCreate + 1);
        VideoList testVideoList = videoListList.get(videoListList.size() - 1);
        assertThat(testVideoList.getVideoListUrlSlug()).isEqualTo(DEFAULT_VIDEO_LIST_URL_SLUG);
    }

    @Test
    @Transactional
    void createVideoListWithExistingId() throws Exception {
        // Create the VideoList with an existing ID
        videoList.setId(1L);

        int databaseSizeBeforeCreate = videoListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVideoListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(videoList)))
            .andExpect(status().isBadRequest());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVideoLists() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        // Get all the videoListList
        restVideoListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(videoList.getId().intValue())))
            .andExpect(jsonPath("$.[*].videoListUrlSlug").value(hasItem(DEFAULT_VIDEO_LIST_URL_SLUG)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVideoListsWithEagerRelationshipsIsEnabled() throws Exception {
        when(videoListRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVideoListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(videoListRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVideoListsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(videoListRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVideoListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(videoListRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVideoList() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        // Get the videoList
        restVideoListMockMvc
            .perform(get(ENTITY_API_URL_ID, videoList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(videoList.getId().intValue()))
            .andExpect(jsonPath("$.videoListUrlSlug").value(DEFAULT_VIDEO_LIST_URL_SLUG));
    }

    @Test
    @Transactional
    void getNonExistingVideoList() throws Exception {
        // Get the videoList
        restVideoListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVideoList() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();

        // Update the videoList
        VideoList updatedVideoList = videoListRepository.findById(videoList.getId()).get();
        // Disconnect from session so that the updates on updatedVideoList are not directly saved in db
        em.detach(updatedVideoList);
        updatedVideoList.videoListUrlSlug(UPDATED_VIDEO_LIST_URL_SLUG);

        restVideoListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVideoList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVideoList))
            )
            .andExpect(status().isOk());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
        VideoList testVideoList = videoListList.get(videoListList.size() - 1);
        assertThat(testVideoList.getVideoListUrlSlug()).isEqualTo(UPDATED_VIDEO_LIST_URL_SLUG);
    }

    @Test
    @Transactional
    void putNonExistingVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, videoList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(videoList))
            )
            .andExpect(status().isBadRequest());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(videoList))
            )
            .andExpect(status().isBadRequest());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(videoList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVideoListWithPatch() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();

        // Update the videoList using partial update
        VideoList partialUpdatedVideoList = new VideoList();
        partialUpdatedVideoList.setId(videoList.getId());

        partialUpdatedVideoList.videoListUrlSlug(UPDATED_VIDEO_LIST_URL_SLUG);

        restVideoListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVideoList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVideoList))
            )
            .andExpect(status().isOk());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
        VideoList testVideoList = videoListList.get(videoListList.size() - 1);
        assertThat(testVideoList.getVideoListUrlSlug()).isEqualTo(UPDATED_VIDEO_LIST_URL_SLUG);
    }

    @Test
    @Transactional
    void fullUpdateVideoListWithPatch() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();

        // Update the videoList using partial update
        VideoList partialUpdatedVideoList = new VideoList();
        partialUpdatedVideoList.setId(videoList.getId());

        partialUpdatedVideoList.videoListUrlSlug(UPDATED_VIDEO_LIST_URL_SLUG);

        restVideoListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVideoList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVideoList))
            )
            .andExpect(status().isOk());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
        VideoList testVideoList = videoListList.get(videoListList.size() - 1);
        assertThat(testVideoList.getVideoListUrlSlug()).isEqualTo(UPDATED_VIDEO_LIST_URL_SLUG);
    }

    @Test
    @Transactional
    void patchNonExistingVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, videoList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(videoList))
            )
            .andExpect(status().isBadRequest());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(videoList))
            )
            .andExpect(status().isBadRequest());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVideoList() throws Exception {
        int databaseSizeBeforeUpdate = videoListRepository.findAll().size();
        videoList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(videoList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VideoList in the database
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVideoList() throws Exception {
        // Initialize the database
        videoListRepository.saveAndFlush(videoList);

        int databaseSizeBeforeDelete = videoListRepository.findAll().size();

        // Delete the videoList
        restVideoListMockMvc
            .perform(delete(ENTITY_API_URL_ID, videoList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<VideoList> videoListList = videoListRepository.findAll();
        assertThat(videoListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
