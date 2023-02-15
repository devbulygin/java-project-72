package hexlet.code.Models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    private long id;

    @WhenCreated
    private Instant whenCreated;

    private int statusCode;



}
