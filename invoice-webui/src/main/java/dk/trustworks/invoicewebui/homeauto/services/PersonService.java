package dk.trustworks.invoicewebui.homeauto.services;

import dk.trustworks.invoicewebui.homeauto.model.List;
import dk.trustworks.invoicewebui.homeauto.model.Person;
import dk.trustworks.invoicewebui.homeauto.model.Room;
import dk.trustworks.invoicewebui.homeauto.model.Root;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PersonService {

    @Autowired UserService userService;

    private final Map<String, Person> personMap;
    private final Map<String, Room> roomMap;

    public PersonService() {
        personMap = new HashMap<>();
        roomMap = new HashMap<>();
    }

    @PostConstruct
    private void createHouse() {

    }

    @RequestMapping("/personseen")
    public void PersonSeen(@RequestParam(value="name") String name, @RequestParam(value = "room") String room) {
        System.out.println("PersonService.PersonSeen");
        System.out.println("name = [" + name + "], room = [" + room + "]");
        if(!personMap.containsKey(name)) personMap.put(name, new Person(name));
        if(!roomMap.containsKey(room)) roomMap.put(room, new Room(room));
        personMap.get(name).addToRoom(roomMap.get(room));
    }

    @RequestMapping("/birthday")
    public java.util.List<List> BirthdayList() {
        java.util.List<List> list = new Root().getList();
        for (User currentlyEmployedUser : userService.findCurrentlyEmployedUsers()) {
            list.add(new List(currentlyEmployedUser.getFirstname() + " "+ currentlyEmployedUser.getLastname(), currentlyEmployedUser.getBirthday().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }
        return list;
    }

    @RequestMapping("/personleft")
    public void PersonLeft(@RequestParam(value="name") String name) {
        System.out.println("PersonService.PersonLeft");
        System.out.println("name = [" + name + "]");
        if(!personMap.containsKey(name)) personMap.put(name, new Person(name));
        personMap.get(name).leftHome();
    }

    @RequestMapping("/locations")
    public String locations() {
        StringBuilder locations = new StringBuilder("[");
        for (Room room : roomMap.values()) {
            locations.append("{room = '").append(room.getName()).append("', persons = ['").append(room.getPersons()).append("']}, ");
            locations.append("{outofhome = '").append(personMap.values().stream().filter(Person::isOutOfHome).map(Person::getName).collect(Collectors.joining("', '"))).append("'}").append("]");
        }
        return locations.toString();
    }
}
