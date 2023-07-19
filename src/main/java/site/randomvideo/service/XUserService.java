package site.randomvideo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.randomvideo.domain.User;
import site.randomvideo.domain.XUser;
import site.randomvideo.repository.XUserRepository;
import site.randomvideo.web.rest.errors.UserNotLoggedInException;
import site.randomvideo.web.rest.errors.XUserNotFoundException;

/**
 * Service class for managing XUsers.
 */
@Service
@Transactional
public class XUserService {
    private final UserService userService;
    private final XUserRepository xUserRepository;

    public XUserService(
        UserService userService,
        XUserRepository xUserRepository
    ) {
        this.userService = userService;
        this.xUserRepository = xUserRepository;
    }

     public XUser getLoggedInXUser() {
        User currentUser = userService.getUserWithAuthorities().orElseThrow(() -> new UserNotLoggedInException());
        return xUserRepository.findOneByInternalUserId(currentUser.getId())
            .orElseThrow(() -> new XUserNotFoundException(currentUser.getId()));
    }
}
