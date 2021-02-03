package me.skiincraft.ousubot.repositories;

import me.skiincraft.beans.stereotypes.RepositoryMap;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.sql.repository.Repository;

@RepositoryMap
public interface ChannelTrackingRepository extends Repository<ChannelTracking, String> {
}
