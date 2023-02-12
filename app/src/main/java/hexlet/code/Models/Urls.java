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

    final String getName() {
        return name;
    }

    final Long getId() {
        return id;
    }

    final Instant getWhenCreated() {
        return whenCreated;
    }
}
