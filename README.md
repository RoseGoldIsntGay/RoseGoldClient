**IF YOU CRASH ON STARTUP BEFORE MINECRAFT LOADS:**
https://www.curseforge.com/minecraft/mc-mods/mixin-0-7-0-8-compatibility/download/3045381

# RoseGoldClient
Cheater get spectated.

### Current Feature List:

<details><summary>Auto Clicker</summary>
  
- Works both in a GUI and outside
- Left click and right click modes
- Customizable speed
- Clicks while key held
</details>

<details><summary>Chest Aura</summary>
  
- Automatically opens nearby chests
- Configurable range
- Chest ESP
</details>

<details><summary>Chest Looter</summary>

- Loot items from chests
- Configurable speed
</details>

<details><summary>Cursor Teleport</summary>

- Teleport to the block you are looking it
- Works upwards through blocks, but not downwards or horizontally
- I hate carpets, and you will too
- Configurable Range
</details>

<details><summary>Dropped Item ESP</summary>

- Highlight dropped items
- Configurable rarities
- Mythic notifications (troll your friends by dropping them mythics)
</details>

<details><summary>Entity ESP</summary>

- Highlight wynncraft entities
- 🌈 Colorful 🌈
- Nametag and distance
- Configurable range
</details>

<details><summary>Entity Ghost Hand</summary>

- Interact with entities through walls
</details>

<details><summary>Kill Aura</summary>
  
- Automatically attack nearby entities
- Configurable modes for each class
- Custom filter with /ka
- Toggle with keybind
</details>

<details><summary>No Rotate</summary>
  
- Prevent the server from setting your rotation
</details>

<details><summary>Phase</summary>
  
- Phase through walls
- Double sneak to descend one block
</details>

<details><summary>Spell Aura</summary>
  
- Automatically attack nearby entities
- Configurable modes for each class
- Custom filter with /sa
- Toggle with keybind
</details>

<details><summary>Spell Caster</summary>
  
- Set a keybind to cast spells automagically
</details>

<details><summary>Velocity</summary>
  
- Change player velocity multiplier
- Configurable with /velocity or in the GUI
</details>

<details><summary>Wynncraft Chest ESP</summary>
  
- Highlight common wynncraft chests used in loot runs
</details>

### Quality of Life Features:

<details><summary>Block Ban Packet</summary>
  
- Automatically block ban packets (working 100%)
</details>

<details><summary>Silent Mode</summary>
  
- Silence all chat messages sent by RGC
- Disable all ESPs
</details>

<details><summary>Anonymize (kys module)</summary>
  
- Remove all text on screen
  - Optional: Randomize text on screen instead of removing it (can be reversed using a vigenere cipher cracker, not 100% safe)
- Set all skins to steve
</details>

### Commands:
#### - /rosegoldclient
- Opens the Config menu
- Aliases: /rgc

#### - /killaura
- Manages the Kill Aura custom filter
- Aliases: /ka
- Arguments: 
  - none: Displays current active filters
  - add {names}: Adds a new word (or multiple) to the filter
  - remove {names}: Removed a word (or multiple) from the filter
  - clear: Clears the filter

#### - /spellaura
- Manages the Spell Aura custom filter
- Aliases: /sa
- Arguments: 
  - none: Displays current active filters
  - add {names}: Adds a new word (or multiple) to the filter
  - remove {names}: Removed a word (or multiple) from the filter
  - clear: Clears the filter

#### - /removechest
- Remove a chest from the wynncraft chest list
- Arguments: 
  - none: Removes closest chest to player
  - coordinates to remove a particular chest

#### - /addchest
- Add a chest to the wynncraft chest list
- Arguments: 
  - none: Add closest chest to player
  - coordinates to add a particular chest

#### - /savechests
- Generate a new .json file containing the new chest list
- Arguments: 
  - none

#### - /selfban
- To test the block ban packet module
- Arguments: 
  - none

#### - /velocity
- Set player velocity multiplier
- Aliases: /velo
- Arguments:
	- x, y, z
