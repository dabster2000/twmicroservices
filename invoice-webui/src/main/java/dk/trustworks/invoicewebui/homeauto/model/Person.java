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
        isHome = true;
        if(this.room != null) this.room.removePerson(this);
        this.room = room;
        this.room.addPerson(this);
    }

    public String getName() {
        return name;
    }

    public boolean isHome() {
        return isHome;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", room=" + room +
                ", isHome=" + isHome +
                '}';
    }

    public void leftHome() {
        isHome = false;
        this.room = null;
    }
}
