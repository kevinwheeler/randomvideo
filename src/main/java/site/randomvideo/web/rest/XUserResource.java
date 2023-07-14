package site.randomvideo.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import site.randomvideo.domain.XUser;
import site.randomvideo.repository.XUserRepository;
import site.randomvideo.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link site.randomvideo.domain.XUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class XUserResource {

    private final Logger log = LoggerFactory.getLogger(XUserResource.class);

    private static final String ENTITY_NAME = "xUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final XUserRepository xUserRepository;

    public XUserResource(XUserRepository xUserRepository) {
        this.xUserRepository = xUserRepository;
    }

    /**
     * {@code POST  /x-users} : Create a new xUser.
     *
     * @param xUser the xUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new xUser, or with status {@code 400 (Bad Request)} if the xUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//    @PostMapping("/x-users")
    public ResponseEntity<XUser> createXUser(@RequestBody XUser xUser) throws URISyntaxException {
        log.debug("REST request to save XUser : {}", xUser);
        if (xUser.getId() != null) {
            throw new BadRequestAlertException("A new xUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        XUser result = xUserRepository.save(xUser);
        return ResponseEntity
            .created(new URI("/api/x-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /x-users/:id} : Updates an existing xUser.
     *
     * @param id the id of the xUser to save.
     * @param xUser the xUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated xUser,
     * or with status {@code 400 (Bad Request)} if the xUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the xUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//    @PutMapping("/x-users/{id}")
    public ResponseEntity<XUser> updateXUser(@PathVariable(value = "id", required = false) final Long id, @RequestBody XUser xUser)
        throws URISyntaxException {
        log.debug("REST request to update XUser : {}, {}", id, xUser);
        if (xUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, xUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!xUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        XUser result = xUserRepository.save(xUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, xUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /x-users/:id} : Partial updates given fields of an existing xUser, field will ignore if it is null
     *
     * @param id the id of the xUser to save.
     * @param xUser the xUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated xUser,
     * or with status {@code 400 (Bad Request)} if the xUser is not valid,
     * or with status {@code 404 (Not Found)} if the xUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the xUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//    @PatchMapping(value = "/x-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<XUser> partialUpdateXUser(@PathVariable(value = "id", required = false) final Long id, @RequestBody XUser xUser)
        throws URISyntaxException {
        log.debug("REST request to partial update XUser partially : {}, {}", id, xUser);
        if (xUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, xUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!xUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<XUser> result = xUserRepository
            .findById(xUser.getId())
            .map(existingXUser -> {
                return existingXUser;
            })
            .map(xUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, xUser.getId().toString())
        );
    }

    /**
     * {@code GET  /x-users} : get all the xUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of xUsers in body.
     */
//    @GetMapping("/x-users")
    public List<XUser> getAllXUsers() {
        log.debug("REST request to get all XUsers");
        return xUserRepository.findAll();
    }

    /**
     * {@code GET  /x-users/:id} : get the "id" xUser.
     *
     * @param id the id of the xUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the xUser, or with status {@code 404 (Not Found)}.
     */
//    @GetMapping("/x-users/{id}")
    public ResponseEntity<XUser> getXUser(@PathVariable Long id) {
        log.debug("REST request to get XUser : {}", id);
        Optional<XUser> xUser = xUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(xUser);
    }

    /**
     * {@code DELETE  /x-users/:id} : delete the "id" xUser.
     *
     * @param id the id of the xUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
//    @DeleteMapping("/x-users/{id}")
    public ResponseEntity<Void> deleteXUser(@PathVariable Long id) {
        log.debug("REST request to delete XUser : {}", id);
        xUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
