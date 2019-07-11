package dk.trustworks.invoicewebui.model;


import dk.trustworks.invoicewebui.model.enums.RoleType;

/**
 * Created by hans on 23/06/2017.
 */
public class Role {

    private String uuid;
    private RoleType role;

    public Role() {
        uuid = "";
    }

    public Role(RoleType role) {
        uuid = "";
        this.role = role;
    }

    public Role(String uuid, RoleType role) {
        System.out.println("NEW ROLE");
        System.out.println("uuid = [" + uuid + "], role = [" + role + "]");
        this.uuid = uuid;
        this.role = role;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" + "uuid='" + uuid + '\'' +
                ", role=" + role +
                '}';
    }
}
