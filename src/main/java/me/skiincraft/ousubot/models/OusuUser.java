package me.skiincraft.ousubot.models;

import me.skiincraft.sql.annotation.Id;
import me.skiincraft.sql.annotation.Table;
import net.dv8tion.jda.api.entities.User;

@Table("tb_users")
public class OusuUser {

    @Id
    private long id;
    private String name;
    private InterfaceType interfaceType;
    private long osuId;

    public OusuUser() {
    }

    public OusuUser(User user) {
        this.id = user.getIdLong();
        this.name = user.getName() + "#" + user.getDiscriminator();
        this.interfaceType = InterfaceType.DEFAULT;
        this.osuId = 0;
    }

    public OusuUser(User user, long userId) {
        this(user);
        this.osuId = 0;
    }

    public long getId() {
        return id;
    }

    public OusuUser setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OusuUser setName(String name) {
        this.name = name;
        return this;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public OusuUser setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
        return this;
    }

    public long getOsuId() {
        return osuId;
    }

    public OusuUser setOsuId(long osuId) {
        this.osuId = osuId;
        return this;
    }

    public enum InterfaceType {
        DEFAULT("Default"), COMPACT("Compact");

        private final String name;

        InterfaceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
