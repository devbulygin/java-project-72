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
    private Long id;

    @WhenCreated
    private Instant whenCreated;

    public Urls() {
    }

    public Urls(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Instant getWhenCreated() {
        return whenCreated;
    }
}
