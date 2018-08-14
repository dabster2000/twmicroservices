package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "budgetnews", path="budgetnews")
public interface BudgetNewRepository extends CrudRepository<BudgetNew, String> {

    /**
     * month starts with 0
     *
     * @param month starts with 0
     * @param year
     * @return
     */
    List<BudgetNew> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
    BudgetNew findByMonthAndYearAndConsultantAndProject(@Param("month") int month, @Param("year") int year, @Param("consultant") Consultant consultant, @Param("project") Project project);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(BudgetNew entity);


}
