package site.randomvideo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import site.randomvideo.web.rest.TestUtil;

class XUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(XUser.class);
        XUser xUser1 = new XUser();
        xUser1.setId(1L);
        XUser xUser2 = new XUser();
        xUser2.setId(xUser1.getId());
        assertThat(xUser1).isEqualTo(xUser2);
        xUser2.setId(2L);
        assertThat(xUser1).isNotEqualTo(xUser2);
        xUser1.setId(null);
        assertThat(xUser1).isNotEqualTo(xUser2);
    }
}
