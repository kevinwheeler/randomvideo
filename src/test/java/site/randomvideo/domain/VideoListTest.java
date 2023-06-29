package site.randomvideo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import site.randomvideo.web.rest.TestUtil;

class VideoListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VideoList.class);
        VideoList videoList1 = new VideoList();
        videoList1.setId(1L);
        VideoList videoList2 = new VideoList();
        videoList2.setId(videoList1.getId());
        assertThat(videoList1).isEqualTo(videoList2);
        videoList2.setId(2L);
        assertThat(videoList1).isNotEqualTo(videoList2);
        videoList1.setId(null);
        assertThat(videoList1).isNotEqualTo(videoList2);
    }
}
