package dk.trustworks.invoicewebui.model;

public class Team {

    private String uuid;
    private String name;
    private String shortname;
    private String logouuid;
    private boolean teamleadbonus;
    private boolean teambonus;

    public Team() {
    }

    public Team(String uuid, String name, String shortname, String logouuid, boolean teamleadbonus, boolean teambonus) {
        this.uuid = uuid;
        this.name = name;
        this.shortname = shortname;
        this.logouuid = logouuid;
        this.teamleadbonus = teamleadbonus;
        this.teambonus = teambonus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getLogouuid() {
        return logouuid;
    }

    public void setLogouuid(String logouuid) {
        this.logouuid = logouuid;
    }

    public boolean isTeamleadbonus() {
        return teamleadbonus;
    }

    public void setTeamleadbonus(boolean teamleadbonus) {
        this.teamleadbonus = teamleadbonus;
    }

    public boolean isTeambonus() {
        return teambonus;
    }

    public void setTeambonus(boolean teambonus) {
        this.teambonus = teambonus;
    }

    @Override
    public String toString() {
        return "Team{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", shortname='" + shortname + '\'' +
                ", logouuid='" + logouuid + '\'' +
                ", teamleadbonus=" + teamleadbonus +
                ", teambonus=" + teambonus +
                '}';
    }
}
