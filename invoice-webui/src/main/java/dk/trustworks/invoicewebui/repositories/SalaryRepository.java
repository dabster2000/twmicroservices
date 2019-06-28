package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Salary;

import java.util.Set;

/**
 * Created by hans on 23/06/2017.
 */

//@RepositoryRestResource(collectionResourceRel = "salaries", path = "salaries")
public interface SalaryRepository {
    void delete(Set<Salary> selectedItems);

    void save(Salary salary);
}
