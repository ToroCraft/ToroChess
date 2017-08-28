
# ToroChess

Play chess in Minecraft with ToroChess!  The ToroChess mod adds chess in a minecrafty way that is fun to play in both creative and survival mode.  The chess pieces are made out of standard Minecraft entities which move and attack like normal mobs.  The chess board can be destroyed and recreated however the player would like, allowing it to become part of the player's builds.  The chess pieces are controlled with eight provided chess control wands, four for each side.

![Screen Shot](http://i.imgur.com/yN4Agb1.png)

## The Chess Control Block

![Chess Control Block](http://i.imgur.com/0bCjFzY.png)

The chess control block is the "brain" of the chess game.  There is nothing special with the blocks of the board.  The board blocks can be destroyed and replaced with whatever blocks the player would like.

A new chess board will be created when you place the control block for the first time.  When created the board is surrounded by quartz blocks and quartz stairs which, of course, can be removed.  The pieces will spawn and move to their correct starting locations just after creation.  The control wands are found in the chests that spawn on the sides of the board along with a short instruction manual.

<a href="http://i.imgur.com/M7egWqR.gifv" target="_blank"><img src="http://i.imgur.com/c0nBYmU.png/"></a>

To reset or clear the board the player will need to gain access to the chess control block.  The control block is not consumed when placed, instead the board is constructed around it.  The control can be found under the center of the board by digging under the board.  Right clicking the control block will display a GUI allowing players to reset or clear the board. The control block can be mined and placed in a more convenient nearby location.  The control block will not regenerate a board when placed subsequent times. 

All chess game information is stored in the control block. To remove the board and chess game, all the player needs to do is mine the control block and board. 

##### Recipe example using quartz and obsidian
![Recipe Example 1](http://i.imgur.com/A32HOAe.png)

##### Recipe example using wood planks
![Recipe Example 1](http://i.imgur.com/oU8ifEv.png)

##### Recipe example using stained glass
![Recipe Example 1](http://i.imgur.com/shGOnqT.png)

Allowed block types for the white and black blocks
- Obsidian
- Wood Planks and Logs
- Glass
- Clay
- Wool
- Dirt
- Quartz
- Stone, Sone Bricks and Cobblestone
- Bone Block
- Coal Block
- Nether Brick
- Melon
- Sandstone

Just because a block is not supported in the recipe doesn't mean you can't have a chessboard made out of it.
If you want a chessboard made out of gold and iron blocks you can simply replace the blocks however you wish.
The blocks must be replaced in the same spot.
For the game to function correctly the blocks also need to be solid so that the chess pieces can walk over it.

## Gameplay

Each side has four control wands that can be used to control the pieces for that side. If the player wants sole control over their side, they can take them all.  The player could also share with friends on their team.  Either way, make sure not to lose them as they cannot be replaced without crafting a new chess control block.

To use a wand, right click a piece while holding the wand in your main hand to select one of your pieces.  Then right click on a square or enemy piece to move to.
The wands work from far away allowing players to build platforms to gain a birds-eye-view in survival or fly around while playing in creative.

To perform a castle move, select the king then shift right click the rook you want to castle with.
Castling will only work when the king and rook have not moved, no pieces are between them and the king will not be in check in any squares it moves through.

Pawns will automatically be promoted to queens when reaching the opposing side of the board.

## Limitations
- En passant is not supported, so your pawns are a little safer ... for now.
- Pawns only promote to queen when reaching the opposite side.

## Future Plans
- En passant support
- Simplistic Chess AI (easy mode only)
- Move timers
- Checkers Mode



## Development Environment Setup

```
git clone git@github.com:ToroCraft/ToroChess.git
cd ToroChess
gradle setupDecompWorkspace
```

To setup an Intellij environment:
```
gradle idea
```

To setup an Eclipse environment:
```
gradle eclipse
```
