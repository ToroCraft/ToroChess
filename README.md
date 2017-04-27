
# ToroChess

Summary ...

![Screen Shot](http://i.imgur.com/yN4Agb1.png)


## The Chess Control Block

![Chess Control Block](http://i.imgur.com/0bCjFzY.png)

Control block explaination ...

Avaliable functions ...

<a href="http://i.imgur.com/M7egWqR.gifv" target="_blank"><img src="http://i.imgur.com/c0nBYmU.png/"></a>



##### Recipe example using quartz and obsidian
![Recipe Example 1](http://i.imgur.com/A32HOAe.png)

##### Recipe example using wood planks
![Recipe Example 1](http://i.imgur.com/oU8ifEv.png)

##### Recipe example using stained glass
![Recipe Example 1](http://i.imgur.com/shGOnqT.png)

Usuable block types for the white and black blocks
- OBSIDIAN
- PLANKS
- PLANKS
- STAINED_GLASS,
- STAINED_HARDENED_CLAY
- WOOL
- DIRT
- QUARTZ_BLOCK
- LOG
- LOG2
- OBSIDIAN
- STONE
- STONEBRICK
- COBBLESTONE
- BONE_BLOCK
- COAL_BLOCK
- NETHER_BRICK
- MELON_BLOCK
- SANDSTONE
- MOSSY_COBBLESTONE

## Gameplay

## Limitations

## Future Plans



## Development Environment Setup
Download the desired version of Forge MDK from https://files.minecraftforge.net/ and unzip the MDK into a new directory. After the MDK is unzipped, clone this repo into the `src` directory as `main`. Then you will need to either copy or link the `build.gradle` from the repository to the root of the MDK, replacing the original one. 

### Setup Example
Replace `<MC_VERSION>` with the Minecraft version of the MDK (for example `~/mdk_1.11.2`) and `<MDK_FILE>` with the file name of the MDK you downloaded (for example `forge-1.10.2-13.20.0.2228-mdk.zip`)

```
mkdir ~/mdk_<MC_VERSION>
cd ~/mdk_<MC_VERSION>
cp <MDK_FILE> .
unzip <MDK_FILE>
rm -rf src/main
git clone git@github.com:ToroCraft/ToroChess.git src/main
mv build.gradle build.default.gradle
ln -s src/main/build.gradle build.gradle
./gradlew setupDecompWorkspace
./gradlew eclipse
```

