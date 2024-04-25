             _______   _______   __    __  ______  _______    ______                        ______   _______   _______  
            |       \ |       \ |  \  |  \|      \|       \  /      \                      /      \ |       \ |       \ 
            | $$$$$$$\| $$$$$$$\| $$  | $$ \$$$$$$| $$$$$$$\|  $$$$$$\                    |  $$$$$$\| $$$$$$$\| $$$$$$$\
            | $$  | $$| $$__| $$| $$  | $$  | $$  | $$  | $$| $$___\$$       ______       | $$__| $$| $$__/ $$| $$__/ $$
            | $$  | $$| $$    $$| $$  | $$  | $$  | $$  | $$ \$$    \       |      \      | $$    $$| $$    $$| $$    $$
            | $$  | $$| $$$$$$$\| $$  | $$  | $$  | $$  | $$ _\$$$$$$\       \$$$$$$      | $$$$$$$$| $$$$$$$ | $$$$$$$ 
            | $$__/ $$| $$  | $$| $$__/ $$ _| $$_ | $$__/ $$|  \__| $$                    | $$  | $$| $$      | $$      
            | $$    $$| $$  | $$ \$$    $$|   $$ \| $$    $$ \$$    $$                    | $$  | $$| $$      | $$      
             \$$$$$$$  \$$   \$$  \$$$$$$  \$$$$$$ \$$$$$$$   \$$$$$$                      \$$   \$$ \$$       \$$      
                                                                                                            
                                                                                                            
                                                                                                            



# Installation
This application, currently, only runs in an emulated environment over the Android Studio IDE. Download the ZIP Archive or clone the repository and open it in Android Studio.
Start the Emulator and run the app. Full functionality is only achieved by also runnning the corresponding server application in the background. [Server Application](https://github.com/SE2-Gruppe-IV-DO2/BackEnd)

# Druids - the implemented game
Druids is a card game that can be played by at least 3 but up to 5 people. There a 65 playing cards, 60 of them are of a certain domain (color), the domains are:
  -  healing arts (green)
  -  prophesy (yellow)
  -  shape-shfiting (red)
  -  astronomy (blue) games
  -  fine-arts (violet)
    
There are 5 special cards, one of them "gaia" determines which player begins, the functionality of "mistletoe" and "golden sickle" shall be determinded later.

## How to play
Whether there are 3, 4 or 5 players the number of cards dealt is different. For 3 players each player get's 15 hand-cards (one of them get's the gaia card), for 4 players 14 cards and for 5 players 13 cards.
The player that has the gaia card begins the round. The player with the gaia card plays it as first turn in the first round and calls a color / domain. The other players have to play that color (if they have it on hand).
As soon as it should be the turn of player who started the round the round stops and the player which played the highest card with the wanted domain get's the "trick". This continues for 5 rounds, where at the end points are caluclated
and a winner is elected.

### Golden Sickle
With the "Golden Sickle" you lose all the experience points of a domain.
If you have a "Golden Sickle" in your trick, you must first discard all domain cards in front of you. 
Then place the "Golden Sickle" on a pile with the highest value. Now set this pile aside with the cards not distributed in this round.
### Mistletoe
With the "mistletoe" you can avoid winning a trick so as not to have to "serve". If you have one or both "Mistletoe" cards in your trick, you 
must first place all the domain cards in front of you. Then place the "Mistletoe" - and also the second
second card if you have received both - to the cards not distributed in this round.

## What is a trick?
Everyone takes it in turns to play exactly one card from their hand onto the table. These cards form the "trick".
The person who starts a trick can play any card from their hand. A card played in this way
must be "served" by all subsequent cards.

**Exception**: If you start a trick with a "Golden Sickle" or a "Mistletoe", the first domain card decides which card is played.
the first domain card that someone places in the trick determines which color must then be played.

## What do to with tricks?
The highest card in the domain (suit) played first wins the trick. Cards in other
colors cannot win a trick, even if their values are higher. Whoever wins the trick
plays the first card for the next trick.

**Attention**: You cannot win a trick with a "Golden Sickle" or a "Mistletoe".
However, if only these cards are in a trick, the person who started it wins the trick.

If you win a trick, you place the cards face up in front of you, sorted by domain (suit).
in front of you. If the trick contains several cards of the same domain, you must place the card with the lowest value on top.
**Note**: Put the Gaia card to one side with the cards not dealt in this round.
You also place new cards from another trick sorted by domains, possibly on top of existing piles.
This adds a new card with a higher or lower value to an existing pile.

## How to calculate points
Each player counts the number displayed on the top of each's domain staple together, this is result of the round.
**Exception**: If there are five domain staples in front of you you get -3 points.

## End of a round
A game round lasts until none of you has a card left in your hand.
However, a round ends immediately if a person has five piles of domains in front of them after winning a trick.
This person receives -3 experience points for this round.
**Note**: A game round also ends if you have all five domains in front of you after a trick, even
though you could still set aside a stack with a "Golden Sickle".
