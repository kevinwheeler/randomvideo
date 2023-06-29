package site.randomvideo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VideoList.
 */
@Entity
@Table(name = "video_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VideoList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "video_list_url_slug")
    private String videoListUrlSlug;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_video_list__video",
        joinColumns = @JoinColumn(name = "video_list_id"),
        inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "videoLists" }, allowSetters = true)
    private Set<Video> videos = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "internalUser", "videoLists" }, allowSetters = true)
    private XUser xUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VideoList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoListUrlSlug() {
        return this.videoListUrlSlug;
    }

    public VideoList videoListUrlSlug(String videoListUrlSlug) {
        this.setVideoListUrlSlug(videoListUrlSlug);
        return this;
    }

    public void setVideoListUrlSlug(String videoListUrlSlug) {
        this.videoListUrlSlug = videoListUrlSlug;
    }

    public Set<Video> getVideos() {
        return this.videos;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

    public VideoList videos(Set<Video> videos) {
        this.setVideos(videos);
        return this;
    }

    public VideoList addVideo(Video video) {
        this.videos.add(video);
        video.getVideoLists().add(this);
        return this;
    }

    public VideoList removeVideo(Video video) {
        this.videos.remove(video);
        video.getVideoLists().remove(this);
        return this;
    }

    public XUser getXUser() {
        return this.xUser;
    }

    public void setXUser(XUser xUser) {
        this.xUser = xUser;
    }

    public VideoList xUser(XUser xUser) {
        this.setXUser(xUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VideoList)) {
            return false;
        }
        return id != null && id.equals(((VideoList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VideoList{" +
            "id=" + getId() +
            ", videoListUrlSlug='" + getVideoListUrlSlug() + "'" +
            "}";
    }
}
