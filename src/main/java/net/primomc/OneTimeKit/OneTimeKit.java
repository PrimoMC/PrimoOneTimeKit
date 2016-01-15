package net.primomc.OneTimeKit;

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

import net.primomc.OneTimeKit.Messages.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static net.primomc.OneTimeKit.Messages.Msg.sendMessagePrefix;

public class OneTimeKit extends JavaPlugin implements CommandExecutor, Listener
{
    private Map<String, List<UUID>> uses;
    private Map<String, KitModel> types;
    private static Plugin instance;
    private UsedConfig usedConfig;
    private MessageHandler messageHandler;

    public static Plugin getInstance()
    {
        return instance;
    }

    private static void setInstance( Plugin instance )
    {
        OneTimeKit.instance = instance;
    }

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        getCommand( "onetime" ).setExecutor( this );
        usedConfig = new UsedConfig( this );
        uses = usedConfig.getUses();
        types = getTypes();
        setInstance( this );
        messageHandler = new MessageHandler( this );
        getServer().getPluginManager().registerEvents( this, this );
    }

    private Map<String, KitModel> getTypes()
    {
        Map<String, KitModel> types = new HashMap<>();
        ConfigurationSection section = getConfig().getConfigurationSection( "types" );
        for ( String key : section.getKeys( false ) )
        {
            if ( section.isConfigurationSection( key ) )
            {
                ConfigurationSection typeSection = section.getConfigurationSection( key );
                List<String> json = typeSection.getStringList( "json" );
                String permission = typeSection.getString( "permission" );
                String message = typeSection.getString( "message" );
                KitModel kitModel = new KitModel( json, permission, message );
                types.put( key.toLowerCase(), kitModel );
            }
        }
        return types;
    }

    @Override
    public void onDisable()
    {

    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event )
    {
        Player player = event.getPlayer();
        String cmdArgs[] = event.getMessage().split( " +" );
        String primaryCmd = cmdArgs[0].trim().toLowerCase().replace( "/", "" );
        if ( types.containsKey( primaryCmd.toLowerCase() ) )
        {
            handleType( player, primaryCmd );
            event.setCancelled( true );
        }
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        if ( command.getName().equalsIgnoreCase( "onetime" ) && sender instanceof Player )
        {
            Player player = (Player) sender;
            OneTimeCommand( player, args );
        }
        return true;
    }

    private void OneTimeCommand( Player player, String[] args )
    {
        if ( args.length < 1 )
        {
            sendMessagePrefix( player, "notenougharguments" );
            return;
        }
        String type = args[0];
        handleType( player, type );
    }

    private void handleType( Player player, String type )
    {
        if ( !types.containsKey( type.toLowerCase() ) )
        {
            sendMessagePrefix( player, "typenotexist", type );
            return;
        }
        KitModel kit = types.get( type.toLowerCase() );
        if ( !player.hasPermission( kit.permission ) )
        {
            sendMessagePrefix( player, "nopermission" );
            return;
        }
        List<UUID> alreadyUsed = new ArrayList<>();
        if ( uses.containsKey( type.toLowerCase() ) )
        {
            alreadyUsed = uses.get( type.toLowerCase() );
        }
        if ( alreadyUsed.contains( player.getUniqueId() ) )
        {
            sendMessagePrefix( player, "alreadyused" );
            return;
        }
        List<ItemStack> items = kit.getItems( this, player );
        HashMap<Integer, ItemStack> excess = player.getInventory().addItem( items.toArray( new ItemStack[items.size()] ) );
        if ( !excess.isEmpty() )
        {
            sendMessagePrefix( player, "inventoryfull" );
            for ( Map.Entry<Integer, ItemStack> me : excess.entrySet() )
            {
                player.getWorld().dropItem( player.getLocation(), me.getValue() );
            }
        }
        alreadyUsed.add( player.getUniqueId() );
        uses.put( type.toLowerCase(), alreadyUsed );
        usedConfig.saveUses( uses );
        player.sendMessage( kit.getMessage() );
    }
}
