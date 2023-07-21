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
import site.randomvideo.domain.User;
import site.randomvideo.domain.Video;
import site.randomvideo.domain.VideoList;
import site.randomvideo.domain.XUser;
import site.randomvideo.repository.VideoRepository;
import site.randomvideo.repository.XUserRepository;
import site.randomvideo.service.UserService;
import site.randomvideo.service.XUserService;
import site.randomvideo.web.rest.errors.BadRequestAlertException;
import site.randomvideo.web.rest.errors.UserNotLoggedInException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link site.randomvideo.domain.Video}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VideoResource {

    private final Logger log = LoggerFactory.getLogger(VideoResource.class);

    private static final String ENTITY_NAME = "video";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;
    private final VideoRepository videoRepository;
    private final XUserRepository xUserRepository;
    private final XUserService xUserService;

    public VideoResource(VideoRepository videoRepository, UserService userService, XUserRepository xUserRepository, XUserService xUserService) {
        this.videoRepository = videoRepository;
        this.userService = userService;
        this.xUserService = xUserService;
        this.xUserRepository = xUserRepository;
    }

    /**
     * {@code POST  /videos} : Create a new video.
     *
     * @param video the video to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new video, or with status {@code 400 (Bad Request)} if the video has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/videos")
    public ResponseEntity<Video> createVideo(@Valid @RequestBody Video video) throws URISyntaxException {
        log.debug("REST request to save Video : {}", video);
        if (video.getId() != null) {
            throw new BadRequestAlertException("A new video cannot already have an ID", ENTITY_NAME, "idexists");
        }
        video.setXUser(xUserService.getLoggedInXUser());

        Video result = videoRepository.save(video);
        return ResponseEntity
            .created(new URI("/api/videos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /videos/:id} : Updates an existing video.
     *
     * @param id the id of the video to save.
     * @param video the video to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated video,
     * or with status {@code 400 (Bad Request)} if the video is not valid,
     * or with status {@code 500 (Internal Server Error)} if the video couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/videos/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Video video)
        throws URISyntaxException {
        log.debug("REST request to update Video : {}, {}", id, video);
        if (video.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, video.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!videoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        XUser currentXUser = xUserService.getLoggedInXUser();
        Video currentVideo = videoRepository.findById(id).get();
        if (!currentVideo.getXUser().equals(currentXUser)) {
//            throw new BadRequestAlertException("You are not allowed to edit this video list", ENTITY_NAME, "notallowed");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Video result = videoRepository.save(video);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, video.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /videos/:id} : Partial updates given fields of an existing video, field will ignore if it is null
     *
     * @param id the id of the video to save.
     * @param video the video to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated video,
     * or with status {@code 400 (Bad Request)} if the video is not valid,
     * or with status {@code 404 (Not Found)} if the video is not found,
     * or with status {@code 500 (Internal Server Error)} if the video couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    // We aren't using patch right now. Will need to add code to make sure that
    // people can't patch other people's videos beforehand if we end using this.
//    @PatchMapping(value = "/videos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Video> partialUpdateVideo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Video video
    ) throws URISyntaxException {
        log.debug("REST request to partial update Video partially : {}, {}", id, video);
        if (video.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, video.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!videoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Video> result = videoRepository
            .findById(video.getId())
            .map(existingVideo -> {
                if (video.getUrl() != null) {
                    existingVideo.setUrl(video.getUrl());
                }
                if (video.getName() != null) {
                    existingVideo.setName(video.getName());
                }

                return existingVideo;
            })
            .map(videoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, video.getId().toString())
        );
    }

    /**
     * {@code GET  /videos} : get videos.
     * @param user if user=current is passed in, only the current user's videos will be returned.
     *      Otherwise, all videos will be returned.
     * @throws BadRequestAlertException if the user is not null or "current"
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of videos in body.
     */
    @GetMapping("/videos")
    public List<Video> getAllVideos(@RequestParam(value = "user", required = false) String user) {
        if (user != null) {
            if (user.equals("current")) {
                log.debug("REST request to get current user's Videos");
                XUser xUser = xUserService.getLoggedInXUser();
                return videoRepository.findByxUserId(xUser.getId());
            } else {
                throw new BadRequestAlertException("Invalid user", ENTITY_NAME, "userinvalid");
            }
        } else {
            log.debug("REST request to get all Videos");
            return videoRepository.findAll();
        }
    }

//    /**
//     * {@code GET  /users/current/videos} : get all the current user's videos.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of videos in body.
//     */
//    @GetMapping("/users/current/videos")
//    public List<Video> getUsersVideos(@PathVariable Long xUserId) {
//        log.debug("REST request to get current user's Videos");
//        XUser xUser = xUserService.getLoggedInXUser();
//        return videoRepository.findByxUserId(xUserId);
//    }


    /**
     * {@code GET  /videos/:id} : get the "id" video.
     *
     * @param id the id of the video to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the video, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/videos/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        log.debug("REST request to get Video : {}", id);
        Optional<Video> video = videoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(video);
    }

    /**
     * {@code DELETE  /videos/:id} : delete the "id" video.
     *
     * @param id the id of the video to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        log.debug("REST request to delete Video : {}", id);
        XUser currentXUser = xUserService.getLoggedInXUser();
        Video videoToDelete = videoRepository.findById(id).get();
        if (!videoToDelete.getXUser().equals(currentXUser)) {
//            throw new BadRequestAlertException("You are not allowed to delete this video list", ENTITY_NAME, "notallowed");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Remove the video from all video lists
        for (VideoList videoList : videoToDelete.getVideoLists()) {
            videoList.getVideos().remove(videoToDelete);
        }
        videoToDelete.getVideoLists().clear();

        videoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
