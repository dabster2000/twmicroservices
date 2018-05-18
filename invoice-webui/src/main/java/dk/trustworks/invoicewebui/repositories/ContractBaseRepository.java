package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Contract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by hans on 23/06/2017.
 */
@NoRepositoryBean
public interface ContractBaseRepository<T extends Contract> extends CrudRepository<T, String> {
}
