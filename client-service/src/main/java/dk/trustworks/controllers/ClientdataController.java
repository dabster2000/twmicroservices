package dk.trustworks.controllers;

import dk.trustworks.model.Client;
import dk.trustworks.model.Clientdata;
import dk.trustworks.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

/**
 * Created by hans on 14/08/2017.
 */

@Transactional
@RepositoryRestController
public class ClientdataController {

    @Autowired
    ClientRepository clientRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/clients/{clientuuid}/clientdata/save", method = RequestMethod.POST)
    public void createClientdata(@PathVariable("clientuuid") String clientuuid, @RequestBody Clientdata clientdata) {
        System.out.println("ClientdataController.createClientdata");
        System.out.println("clientdata = [" + clientdata + "]");
        clientdata.setUuid(UUID.randomUUID().toString());
        Client client = clientRepository.findOne(clientuuid);
        clientdata.setClient(client);
        entityManager.persist(clientdata);
    }

}
