package net.primomc.OneTimeKit.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright 2015 Luuk Jacobs
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Messages
{
    private static Map<Plugin, Map<String, String>> messages = new HashMap<>();

    private static String msg( boolean hasPrefix, Plugin plugin, String modifier, String... values )
    {
        if ( !messages.containsKey( plugin ) )
        {
            return ChatColor.RED + "String doesn't exist in config.";
        }
        String message;
        if ( !messages.get( plugin ).containsKey( modifier ) )
        {
            return ChatColor.RED + "String doesn't exist in config.";
        }
        else
        {
            message = messages.get( plugin ).get( modifier );
        }
        String prefix = "";
        if ( hasPrefix && messages.get( plugin ).containsKey( "prefix" ) )
        {
            prefix = messages.get( plugin ).get( "prefix" );
        }
        int i = 0;
        for ( String value : values )
        {
            message = message.replaceAll( "\\{" + i + "\\}", Matcher.quoteReplacement( value ) );
            i++;
        }
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes( '&', message );
        return message;
    }

    private static String msg( String message )
    {
        message = ChatColor.translateAlternateColorCodes( '&', message );
        return message;
    }

    public static JChat jsonReplacer( String message )
    {
        message = message.replaceAll( "\\n", "||||" );
        JChat jchat = new JChat( "" );
        String regex = "(?i)\\[(hover-text|click-url|click-command|hover-command|hover-url)=(.+)\\](.+)\\[/\\1\\]";
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( message );
        List<String> bbcodes = new ArrayList<>();
        while ( matcher.find() )
        {
            bbcodes.add( matcher.group() );
        }
        if ( bbcodes.size() > 0 )
        {
            String remainder = message;
            for ( String bbcode : bbcodes )
            {
                String[] split = remainder.split( Pattern.quote( bbcode ), 2 );
                jchat.then( split[0] );
                if ( split.length == 2 )
                {
                    remainder = split[1];
                }
                String param = bbcode.replaceFirst( regex, "$2" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                String text = bbcode.replaceFirst( regex, "$3" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                jchat.then( text );
                if ( bbcode.toLowerCase().contains( "[click-command=" ) )
                {
                    jchat.command( param );
                }
                else if ( bbcode.toLowerCase().contains( "[click-url=" ) )
                {
                    jchat.link( param );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-text=" ) )
                {
                    jchat.tooltip( param );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-command=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.tooltip( params[0] ).command( params[1] );
                    }
                }
                else if ( bbcode.toLowerCase().contains( "[hover-url=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.tooltip( params[0] ).link( params[1] );
                    }
                }
            }
            jchat.then( remainder );
        }
        else
        {
            jchat = jchat.then( message.replaceAll( "\\|\\|\\|\\|", "\n" ) );
        }
        return jchat;
    }

    public static String getMessageJson( Plugin plugin, String key, String... values )
    {
        return jsonReplacer( msg( false, plugin, key, values ) ).toJSONString();
    }

    public static String getBasicMessage( String message )
    {
        return msg( message );
    }

    public static String getBasicMessage( Plugin plugin, boolean hasPrefix, String key, String... values )
    {
        return msg( hasPrefix, plugin, key, values );
    }

    public static void sendMessage( Plugin plugin, Player[] players, String key, String... values )
    {
        JChat jchat = jsonReplacer( msg( true, plugin, key, values ) );
        for ( Player player : players )
        {
            try
            {
                jchat.send( player );
            }
            catch ( Exception ignored )
            {
            }
        }
    }

    public static void sendMessage( boolean hasPrefix, Plugin plugin, Player[] players, String key, String... values )
    {
        JChat jchat = jsonReplacer( msg( hasPrefix, plugin, key, values ) );
        for ( Player player : players )
        {
            try
            {
                jchat.send( player );
            }
            catch ( Exception ignored )
            {
            }
        }
    }

    public static void sendMessage( Plugin plugin, Player player, String key, String[] values )
    {
        sendMessage( plugin, new Player[]{ player }, key, values );
    }

    public static void sendMessage( Plugin plugin, CommandSender sender, String key, String[] values )
    {
        if ( sender instanceof Player )
        {
            sendMessage( plugin, new Player[]{ (Player) sender }, key, values );
        }
        else
        {
            sender.sendMessage( msg( true, plugin, key, values ) );
        }
    }

    public static void setMessages( Plugin plugin, Map<String, String> messages )
    {
        Messages.messages.put( plugin, messages );
    }
}
