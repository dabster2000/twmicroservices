package dk.trustworks.invoicewebui.model;


import dk.trustworks.invoicewebui.model.enums.KnowledgeRoleType;

/**
 * Created by hans on 23/06/2017.
 */
public class KnowledgeRole {

    private int id;
    private String useruuid;
    private KnowledgeRoleType name;

    public KnowledgeRole() {
    }

    public KnowledgeRole(int id, String useruuid, KnowledgeRoleType name) {
        this.id = id;
        this.useruuid = useruuid;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public KnowledgeRoleType getName() {
        return name;
    }

    public void setName(KnowledgeRoleType name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "KnowledgeRole{" +
                "id=" + id +
                ", useruuid='" + useruuid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
