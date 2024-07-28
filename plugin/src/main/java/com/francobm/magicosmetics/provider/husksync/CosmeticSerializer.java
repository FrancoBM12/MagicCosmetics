package com.francobm.magicosmetics.provider.husksync;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitSerializer;
import net.william278.husksync.data.Serializer;

public class CosmeticSerializer extends BukkitSerializer implements Serializer<CosmeticData> {


    public CosmeticSerializer(HuskSyncAPI plugin) {
        super(plugin);
    }

    @Override
    public CosmeticData deserialize(String s) {
        return plugin.getDataAdapter().fromJson(s, CosmeticData.class);
    }

    @Override
    public String serialize(CosmeticData cosmeticData) throws SerializationException {
        return plugin.getDataAdapter().toJson(cosmeticData);
    }
}
