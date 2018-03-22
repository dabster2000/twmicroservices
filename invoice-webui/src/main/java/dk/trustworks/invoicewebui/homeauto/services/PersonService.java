package dk.trustworks.invoicewebui.homeauto.services;

import dk.trustworks.invoicewebui.homeauto.events.RoomEmptyEventHandler;
import dk.trustworks.invoicewebui.homeauto.model.Person;
import dk.trustworks.invoicewebui.homeauto.model.Room;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PersonService {

    private final Map<String, Person> personMap;
    private final Map<String, Room> roomMap;

    public PersonService() {
        personMap = new HashMap<>();
        roomMap = new HashMap<>();
    }

    @PostConstruct
    private void createHouse() {
        Person hans = new Person("Hans");
        Person camilla = new Person("Camilla");
        personMap.put("hans", hans);
        personMap.put("camilla", camilla);

        Room livingroom = new Room("Living Room");
        Room kitchen = new Room("Kitchen");
        kitchen.registerEmptyHandler(new RoomEmptyEventHandler(kitchen));
        roomMap.put("kitchen", kitchen);
        roomMap.put("livingroom", livingroom);

        hans.addToRoom(kitchen);
    }

    @RequestMapping("/personseen")
    public void PersonSeen(@RequestParam(value="name") String name, @RequestParam(value = "room") String room) {
        System.out.println("PersonService.PersonSeen");
        System.out.println("name = [" + name + "], room = [" + room + "]");
        personMap.get(name).addToRoom(roomMap.get(room));
    }
}
