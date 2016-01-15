package net.primomc.OneTimeKit;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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
public class KitModel
{
    List<String> jsonStrings;
    String permission;
    private String message;

    public KitModel( List<String> jsonStrings, String permission, String message )
    {
        this.jsonStrings = jsonStrings;
        this.permission = permission;
        this.message = message;
    }

    public List<String> getJsonStrings()
    {
        return jsonStrings;
    }

    public List<ItemStack> getItems(Plugin plugin, Player player)
    {
        List<ItemStack> stacks = new ArrayList<>();
        for ( String json : jsonStrings )
        {
            String[] split = json.split( " ", 4 );
            if ( split.length < 4 )
            {
                continue;
            }
            String sMaterial = split[0];
            short damage = 0;
            try
            {
                damage = Short.parseShort( split[2] );
            }
            catch ( NumberFormatException ex )
            {
                damage = 0;
            }
            Material material = Bukkit.getServer().getUnsafe().getMaterialFromInternalName( sMaterial );
            if ( material == null )
            {
                continue;
            }
            int amount;

            try
            {
                amount = Integer.parseInt( split[1] );
            }
            catch ( NumberFormatException ex )
            {
                amount = 1;
            }

            ItemStack stack = new ItemStack( material, amount, damage );
            plugin.getServer().getUnsafe().modifyItemStack( stack, PlaceholderAPI.setPlaceholders( player, ChatColor.translateAlternateColorCodes( '&', split[3] ) ) );
            stacks.add( stack );
        }
        return stacks;
    }

    public String getPermission()
    {
        return permission;
    }

    public String getMessage()
    {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
