package hexlet.code.Models;

import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class Urls {
    private String name;
    private String createdAt;
    @Id
    private Long id;

    @WhenCreated
    private Instant whenCreated;

}
