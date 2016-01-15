package net.primomc.OneTimeKit;

import org.bukkit.plugin.Plugin;

import java.util.*;

/*
 * Copyright 2016 Luuk Jacobs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class UsedConfig extends CommonsConfig
{
    public UsedConfig( Plugin plugin )
    {
        super( plugin, "used.yml" );
    }

    public Map<String, List<UUID>> getUses()
    {
        Map<String, List<UUID>> uses = new HashMap<>();
        for ( String key : config.getKeys( false ) )
        {
            List<UUID> uuids;
            if ( uses.containsKey( key ) )
            {
                uuids = uses.get( key );
            }
            else
            {
                uuids = new ArrayList<>();
            }
            List<String> sList = config.getStringList( key );
            for ( String item : sList )
            {
                try
                {
                    UUID uuid = UUID.fromString( item );
                    uuids.add( uuid );
                }
                catch ( Exception ignored )
                {
                }
            }
            uses.put( key, uuids );
        }
        return uses;
    }

    public void saveUses(Map<String, List<UUID>> uses)
    {
        for(String key : uses.keySet())
        {
            List<String> uuids = new ArrayList<>();
            for(UUID uuid : uses.get( key ))
            {
                uuids.add( uuid.toString() );
            }
            config.set( key, uuids );
        }
        saveConfig();
    }
}
