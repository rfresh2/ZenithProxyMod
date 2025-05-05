# ZenithProxy Mod Integration

MC client mod integration for [ZenithProxy](https://github.com/rfresh2/ZenithProxy)

# Features

## Join As Spectator Button

Adds a button to the server list GUI for ZenithProxy servers that allows you to join as a spectator.

<p align="center">
  <img src="https://i.imgur.com/DEGg1GV.png">
</p>

## Disconnect And Spectator Swap Buttons

Adds a button to the escape menu that sends `/disconnect` or `/swap` commands

`/disconnect` makes the ZenithProxy account also disconnect.

`/swap` makes the ZenithProxy instance swap you into spectator mode

<p align="center">
  <img src="https://i.imgur.com/VBAAa1J.png">
</p>

## Web API Commands

**Requires [ZenithProxyWebAPI](https://github.com/rfresh2/ZenithProxyWebAPI)**

Sends commands to a ZenithProxy instance over the internet.

For example, using `pearlLoader load` to make another bot load your pearl.

`/api add <id> <ip> <token>` -> Add a web API. The ip should include the port. You may need to enclose arguments in quotes. like `"localhost:8080"`

`/api del <id>` -> Remove a web API

`/api list` -> List all web API's

`/api command <id> <command>` -> Send a command
