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

    public UrlCheck(int statusCode, String title, String h1, String description, Urls url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    /**
     * this method returns id field.
     *
     * @return the id URL in database, is int
     */
    public long getId() {
        return id;
    }

    /**
     * this method returns created date field.
     *
     * @return the date and time when the URL was created, in ISO 8601 format
     */
    public Instant getWhenCreated() {
        return whenCreated;
    }

    /**
     * this method returns status code http request.
     *
     * @return the status code in Long
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * this method returns title tag on the web page.
     *
     * @return the String title
     */
    public String getTitle() {
        return title;
    }

    /**
     * this method returns h1 tag on the web page.
     *
     * @return the String h1
     */
    public String getH1() {
        return h1;
    }

    /**
     * this method returns meta tag with argument name with paremeter description and argument content tag on web page.
     *
     * @return the String descrition
     */
    public String getDescription() {
        return description;
    }

    /**
     * this method linked table urls many to one.
     *
     * @return Urls object
     */

    public Urls getUrl() {
        return url;
    }

}
