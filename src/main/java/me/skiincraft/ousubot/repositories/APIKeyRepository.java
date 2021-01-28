package me.skiincraft.ousubot.repositories;

import me.skiincraft.beans.stereotypes.RepositoryMap;
import me.skiincraft.ousubot.models.APIKey;
import me.skiincraft.sql.repository.Repository;

@RepositoryMap
public interface APIKeyRepository extends Repository<APIKey, String> {
}
