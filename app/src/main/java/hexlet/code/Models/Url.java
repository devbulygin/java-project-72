package hexlet.code.Models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {
    private String name;
    @Id
    private long id;

    @WhenCreated
    private Instant created_at;

    @OneToMany(mappedBy = "url")
    private List<UrlCheck> checks;

    public Url() {
    }

    public Url(String name) {

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
     * this method returns created date field.
     *
     * @return the date and time when the URL was created, in ISO 8601 format
     */

    public Instant getCreated_at() {
        return created_at;
    }

    /**
     this method returns created List of checks.
     @return the List
     */
    public List<UrlCheck> getChecks() {
        return checks;
    }
    /**
     this method add check url in check.

     @param check add UrlCheck object after urlCheck operation.
     */

    public void addCheck(UrlCheck check) {
        checks.add(check);
    }
}
