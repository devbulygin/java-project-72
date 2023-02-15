package hexlet.code.Models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class Urls extends Model {
    private String name;
    @Id
    private long id;

    @WhenCreated
    private Instant whenCreated;

    public Urls() {
    }

    public Urls(String name) {

        this.name = name;
    }

    /**
     this method returns name field.
     @return the URL String in database
     */
    public String getName() {
        return name;
    }
    /**
     this method returns id field.
     @return the id URL in database, in Long
     */
    public long getId() {
        return id;
    }

    /**
     this method returns created date field.
     @return the date and time when the URL was created, in ISO 8601 format
     */

    public Instant getWhenCreated() {
        return whenCreated;
    }
}
