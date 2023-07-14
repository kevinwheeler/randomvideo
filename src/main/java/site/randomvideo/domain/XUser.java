package site.randomvideo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A XUser.
 */
@Entity
@Table(name = "x_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class XUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User internalUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "xUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "videos", "xUser" }, allowSetters = true)
    private Set<VideoList> videoLists = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "xUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "xUser", "videoLists" }, allowSetters = true)
    private Set<Video> videos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public XUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public XUser internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public Set<VideoList> getVideoLists() {
        return this.videoLists;
    }

    public void setVideoLists(Set<VideoList> videoLists) {
        if (this.videoLists != null) {
            this.videoLists.forEach(i -> i.setXUser(null));
        }
        if (videoLists != null) {
            videoLists.forEach(i -> i.setXUser(this));
        }
        this.videoLists = videoLists;
    }

    public XUser videoLists(Set<VideoList> videoLists) {
        this.setVideoLists(videoLists);
        return this;
    }

    public XUser addVideoList(VideoList videoList) {
        this.videoLists.add(videoList);
        videoList.setXUser(this);
        return this;
    }

    public XUser removeVideoList(VideoList videoList) {
        this.videoLists.remove(videoList);
        videoList.setXUser(null);
        return this;
    }

    public Set<Video> getVideos() {
        return this.videos;
    }

    public void setVideos(Set<Video> videos) {
        if (this.videos != null) {
            this.videos.forEach(i -> i.setXUser(null));
        }
        if (videos != null) {
            videos.forEach(i -> i.setXUser(this));
        }
        this.videos = videos;
    }

    public XUser videos(Set<Video> videos) {
        this.setVideos(videos);
        return this;
    }

    public XUser addVideo(Video video) {
        this.videos.add(video);
        video.setXUser(this);
        return this;
    }

    public XUser removeVideo(Video video) {
        this.videos.remove(video);
        video.setXUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XUser)) {
            return false;
        }
        return id != null && id.equals(((XUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "XUser{" +
            "id=" + getId() +
            "}";
    }
}
