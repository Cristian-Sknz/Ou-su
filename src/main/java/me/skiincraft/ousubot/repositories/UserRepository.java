package me.skiincraft.ousubot.repositories;

import me.skiincraft.beans.stereotypes.RepositoryMap;
import me.skiincraft.ousubot.models.OusuUser;
import me.skiincraft.sql.repository.Repository;

@RepositoryMap
public interface UserRepository extends Repository<OusuUser, Long> {
}
