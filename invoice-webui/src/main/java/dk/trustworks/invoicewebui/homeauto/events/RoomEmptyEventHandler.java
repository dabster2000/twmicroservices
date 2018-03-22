package dk.trustworks.invoicewebui.homeauto.events;

import dk.trustworks.invoicewebui.homeauto.model.Room;
import org.springframework.web.client.RestTemplate;

public class RoomEmptyEventHandler {

    final String key = "beMOOD71a6r0Dw-Jfa-Y5-";
    final Room room;

    public RoomEmptyEventHandler(Room room) {
        this.room = room;
    }

    public void fire() {
        System.out.println("RoomEmptyEventHandler.fire");
        new RestTemplate().getForObject("https://maker.ifttt.com/trigger/kitchen_empty/with/key/"+key, String.class);
    }
}
