package dk.trustworks.invoicewebui.homeauto.model;

import dk.trustworks.invoicewebui.homeauto.events.RoomEmptyEventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Room {

    private final List<RoomEmptyEventHandler> roomEmptyEventHandlerList;
    private final String name;
    private final Set<Person> personSet;

    public Room(String name) {
        this.name = name;
        personSet = new HashSet<>();
        roomEmptyEventHandlerList = new ArrayList<>();
    }

    protected void removePerson(Person person) {
        System.out.println("Room.removePerson");
        System.out.println("person = [" + person + "]");
        personSet.remove(person);
        if(personSet.isEmpty()) {
            roomEmptyEventHandlerList.stream().forEach(RoomEmptyEventHandler::fire);
        }
    }

    public void registerEmptyHandler(RoomEmptyEventHandler roomEmptyEventHandler) {
        roomEmptyEventHandlerList.add(roomEmptyEventHandler);
    }

    public void addPerson(Person person) {
        System.out.println("Room.addPerson");
        System.out.println("person = [" + person + "]");
        personSet.add(person);
    }

    public String getName() {
        return name;
    }

    public String getPersons() {
        return personSet.stream().map(Person::getName).collect(Collectors.joining("', '"));
    }

    @Override
    public String toString() {
        return "Room{" +
                ", name='" + name + '\'' +
                '}';
    }
}
