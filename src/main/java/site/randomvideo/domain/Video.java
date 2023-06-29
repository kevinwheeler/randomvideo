package site.randomvideo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Video.
 */
@Entity
@Table(name = "video")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "videos")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "videos", "xUser" }, allowSetters = true)
    private Set<VideoList> videoLists = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Video id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public Video url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<VideoList> getVideoLists() {
        return this.videoLists;
    }

    public void setVideoLists(Set<VideoList> videoLists) {
        if (this.videoLists != null) {
            this.videoLists.forEach(i -> i.removeVideo(this));
        }
        if (videoLists != null) {
            videoLists.forEach(i -> i.addVideo(this));
        }
        this.videoLists = videoLists;
    }

    public Video videoLists(Set<VideoList> videoLists) {
        this.setVideoLists(videoLists);
        return this;
    }

    public Video addVideoList(VideoList videoList) {
        this.videoLists.add(videoList);
        videoList.getVideos().add(this);
        return this;
    }

    public Video removeVideoList(VideoList videoList) {
        this.videoLists.remove(videoList);
        videoList.getVideos().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Video)) {
            return false;
        }
        return id != null && id.equals(((Video) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Video{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
