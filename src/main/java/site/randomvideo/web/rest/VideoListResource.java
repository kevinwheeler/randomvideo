package site.randomvideo.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import site.randomvideo.domain.VideoList;
import site.randomvideo.domain.XUser;
import site.randomvideo.repository.XUserRepository;
import site.randomvideo.repository.VideoListRepository;
import site.randomvideo.service.UserService;
import site.randomvideo.service.XUserService;
import site.randomvideo.web.rest.errors.BadRequestAlertException;
import site.randomvideo.web.rest.errors.UserNotLoggedInException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link site.randomvideo.domain.VideoList}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VideoListResource {

    private final Logger log = LoggerFactory.getLogger(VideoListResource.class);

    private static final String ENTITY_NAME = "videoList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VideoListRepository videoListRepository;
    private final UserService userService;
    private final XUserService xUserService;

    private final XUserRepository xUserRepository;



    public VideoListResource(VideoListRepository videoListRepository, UserService userService, XUserService xUserService, XUserRepository xUserRepository) {
        this.videoListRepository = videoListRepository;
        this.userService = userService;
        this.xUserService = xUserService;
        this.xUserRepository = xUserRepository;
    }

    /**
     * {@code POST  /video-lists} : Create a new videoList.
     *
     * @param videoList the videoList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new videoList, or with status {@code 400 (Bad Request)} if the videoList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws UserNotLoggedInException if the user is not logged in.
     */
    @PostMapping("/video-lists")
    public ResponseEntity<VideoList> createVideoList(@Valid @RequestBody VideoList videoList) throws URISyntaxException {
        log.debug("REST request to save VideoList : {}", videoList);
        if (videoList.getId() != null) {
            throw new BadRequestAlertException("A new videoList cannot already have an ID", ENTITY_NAME, "idexists");
        }

        videoList.setXUser(xUserService.getLoggedInXUser());

        VideoList result = videoListRepository.save(videoList);
        return ResponseEntity
            .created(new URI("/api/video-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /video-lists/:id} : Updates an existing videoList.
     *
     * @param id the id of the videoList to save.
     * @param videoList the videoList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated videoList,
     * or with status {@code 400 (Bad Request)} if the videoList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the videoList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/video-lists/{id}")
    public ResponseEntity<VideoList> updateVideoList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VideoList videoList
    ) throws URISyntaxException {
        log.debug("REST request to update VideoList : {}, {}", id, videoList);
        if (videoList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, videoList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!videoListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        XUser currentXUser = xUserService.getLoggedInXUser();
        VideoList currentVideoList = videoListRepository.findById(id).get();
        if (!currentVideoList.getXUser().equals(currentXUser)) {
//            throw new BadRequestAlertException("You are not allowed to edit this video list", ENTITY_NAME, "notallowed");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        videoList.setXUser(currentXUser);
        VideoList result = videoListRepository.save(videoList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, videoList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /video-lists/:id} : Partial updates given fields of an existing videoList, field will ignore if it is null
     *
     * @param id the id of the videoList to save.
     * @param videoList the videoList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated videoList,
     * or with status {@code 400 (Bad Request)} if the videoList is not valid,
     * or with status {@code 404 (Not Found)} if the videoList is not found,
     * or with status {@code 500 (Internal Server Error)} if the videoList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//     commented out since we aren't using it currently. If we need it later, we'll need
//     to make sure only the owner of the video list can update it.
//    @PatchMapping(value = "/video-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VideoList> partialUpdateVideoList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VideoList videoList
    ) throws URISyntaxException {
        log.debug("REST request to partial update VideoList partially : {}, {}", id, videoList);
        if (videoList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, videoList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!videoListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VideoList> result = videoListRepository
            .findById(videoList.getId())
            .map(existingVideoList -> {
                if (videoList.getName() != null) {
                    existingVideoList.setName(videoList.getName());
                }
                if (videoList.getSlug() != null) {
                    existingVideoList.setSlug(videoList.getSlug());
                }

                return existingVideoList;
            })
            .map(videoListRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, videoList.getId().toString())
        );
    }

    /**
     * {@code GET  /video-lists} : get all the videoLists.
     * @param user if user=current is passed in, only the current user's videos will be returned.
     *      Otherwise, all videos will be returned.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of videoLists in body.
     */
    @GetMapping("/video-lists")
    public List<VideoList> getAllVideoLists(@RequestParam(required = false, defaultValue = "false") boolean eagerload, @RequestParam(value = "user", required = false) String user) {
        if (user != null){
            if (user.equals("current")){
                XUser currentXUser = xUserService.getLoggedInXUser();
                log.debug("REST request to get all VideoLists for current user");
                if (eagerload) {
                    return videoListRepository.findAllWithEagerRelationshipsByxUserId(currentXUser.getId());
                } else {
                    return videoListRepository.findAllByxUserId(currentXUser.getId());
                }
            } else {
                throw new BadRequestAlertException("Invalid user", ENTITY_NAME, "userinvalid");
            }
        } else {
            log.debug("REST request to get all VideoLists");
            if (eagerload) {
                return videoListRepository.findAllWithEagerRelationships();
            } else {
                return videoListRepository.findAll();
            }
        }
    }

    /**
     * {@code GET  /video-lists/:id} : get the "id" videoList.
     *
     * @param id the id of the videoList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the videoList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/video-lists/{id}")
    public ResponseEntity<VideoList> getVideoList(@PathVariable Long id) {
        log.debug("REST request to get VideoList : {}", id);
        Optional<VideoList> videoList = videoListRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(videoList);
    }

    /**
     * {@code DELETE  /video-lists/:id} : delete the "id" videoList.
     *
     * @param id the id of the videoList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/video-lists/{id}")
    public ResponseEntity<Void> deleteVideoList(@PathVariable Long id) {
        log.debug("REST request to delete VideoList : {}", id);

        XUser currentXUser = xUserService.getLoggedInXUser();
        VideoList videoListToDelete = videoListRepository.findById(id).get();
        if (!videoListToDelete.getXUser().equals(currentXUser)) {
//            throw new BadRequestAlertException("You are not allowed to delete this video list", ENTITY_NAME, "notallowed");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        videoListRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
