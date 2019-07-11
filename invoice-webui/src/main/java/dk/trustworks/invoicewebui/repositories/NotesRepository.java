package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "note", path = "note")
public interface NotesRepository extends CrudRepository<Note, Integer> {

    List<Note> findAll();
    List<Note> findByUseruuidOrderByNotedateDesc(@Param("user") String user);

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(Note entity);

}
