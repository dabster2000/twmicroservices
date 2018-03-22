package dk.trustworks.invoicewebui.homeauto.model;

public class Person {

    final String name;
    Room room;
    boolean isHome;

    public Person(String name) {
        this.name = name;
        isHome = false;
        room = null;
    }

    public void addToRoom(Room room) {
        if(room.equals(this.room)) return;
        if(this.room != null) this.room.removePerson(this);
        this.room = room;
        this.room.addPerson(this);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", room=" + room +
                ", isHome=" + isHome +
                '}';
    }
}
