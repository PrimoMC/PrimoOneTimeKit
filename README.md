# Primo OneTimeKit

A plugin that gives people kits that they can only get once, and never again.
Great for limited-time event items.
Each kit has its own permissions, and the items utilize the minecraft /give json format.

## Installation instructions:
1. Download and install PlaceholderAPI from https://www.spigotmc.org/resources/placeholderapi.6245/
2. Download and install PrimoOneTimeKit.
3. Start and stop the server to create the configs.
3. Configure PrimoOneTimeKit and PlaceholderAPI.
7. You're done.

## default config
The config.yml has explanations for configuring the plugin.

```yaml
types:
  #the type of kit.
  heart:
    #the permission people need to use the kit
    permission: 'onetime.heart'
    # The items in the kit.
    # The items utilize the minecraft give format, you can use https://ezekielelin.com/give/ to create the items
    # You can use Placeholders in the lore and displayname fields like so: http://i.imgur.com/o9kixW2.png  and http://i.imgur.com/JShUh9a.png
    # You copy the resulting give command in the config http://i.imgur.com/b06SpZ5.png
    # important things to note:
    #     a single-quote has to be used twice, otherwise it will break the yaml.
    #     Make sure to remove the /give @a parts of the give command, otherwise it won't work.
    #     You can add colour by using colourcodes like &4, &f, &r, etc.
    #     You can have multiple lines to give multiple items.
    items:
    - 'minecraft:nether_star 1 0 {ench:[{id:0,lvl:6}],display:{Name:"&6%player_name%''s heart",Lore:["&4Custom Lore!","&aLOOOOOREEEE"]}}'
    #The message that gets sent to the player upon receiving the kit.
    message: "&4You spawned yourself a heart!"
  easter:
    permission: 'onetime.easter'
    items:
    #an example of using multiple lines
    - 'minecraft:spawn_egg 1 101 {ench:[{id:19,lvl:1}],AttributeModifiers:[{AttributeName:"generic.attackDamage",Name:"generic.attackDamage",Amount:8,Operation:0,UUIDMost:96579,UUIDLeast:117351}],display:{Name:"&6Easter Bunny",Lore:["&aSpawn yourself an easter bunny!"]}}'
    - 'minecraft:spawn_egg 1 101 {ench:[{id:19,lvl:1}],AttributeModifiers:[{AttributeName:"generic.attackDamage",Name:"generic.attackDamage",Amount:8,Operation:0,UUIDMost:96579,UUIDLeast:117351}],display:{Name:"&6Easter Bunny",Lore:["&aSpawn yourself an easter bunny!"]}}'
    message: "&4You spawned yourself an easter egg!"
```
