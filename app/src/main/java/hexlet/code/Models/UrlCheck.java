package hexlet.code.Models;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    private long id;

    @WhenCreated
    private Instant whenCreated;

    private int statusCode;

    private String title;

    private String h1;

    @Lob
    private String description;

    @ManyToOne
    @NotNull
    private Urls url;

    public UrlCheck() {
    }

    public UrlCheck(int statusCode, Urls url) {
        this.statusCode = statusCode;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public Instant getWhenCreated() {
        return whenCreated;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public Urls getUrl() {
        return url;
    }




}
