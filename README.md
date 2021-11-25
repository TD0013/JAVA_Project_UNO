# UNO
This repository contains the code developed by [Sushrut Patwardhan](f20190382@pilani.bits-pilani.ac.in) and [Tanishq Duhan](f20190636@pilani.bits-pilani.ac.in) for the group assignment : Build UNO, for the course Object Oriented Programming in JAVA.

## Table of Contents
* [How to Use](#how-to-use)
* [Helper Document](#helper-document)
* [Analysis of Project](#analysis-of-project)


## How to Use
This repository contains multiple files and folders which contain the same code in multiple formats:
* The Folder "Build" contains the the project in bytecode.
* The File "UNO.java" contains the project in .java format and can be compiled into byteCode.
* The File "UNO.jar" contains the file in jar format and this code needs to be extracted before use. The extracted file contains a "UNO.java" file that can be can be compiled into byteCode.

## [Helper Document](https://docs.google.com/document/d/1RX_772KRHujQt6YojQMUvlOEX8ROps4PbVOHyAWTbG0/edit#)

* First the startGame method asks us to add the number of players in the game.

* The first prompt that we get is “Enter Number of players (between 2-5):”, in which the user can enter the number of players.
For the following example, we choose the number 2.
Next, we take the names of all players to create and assign to the new Player Objects.

* As soon as we enter the player details, the startGame method initializes and shuffles the deck of all cards. It then distributes 7 cards to players and fetches the top card for the play to begin. In case the top card is a special card, the deck is shuffled again and the top card is fetched again. This goes on until the top card is a normal card. 

* At all times and for all turns, the top card is displayed. The cards of the current player are also displayed.

* According to the rules of UNO, if the player has a card that has the same number or colour as the top card, he may play such a card, else he can draw a card. If the player draws a card and gets a valid playable card, he can play this card else he can pass his turn.
There can be 2 scenarios for the player:
The Player has a playable card: The player is asked to play a card.

The player does not have a card he can play: One card is automatically drawn from the deck for the player. 

There can be 2 further scenarios:
The player now has a playable card: The player is asked to play a card.

The player still does not have a playable card: The player’s current turn is skipped automatically.

* If the player enters the card in a different format, he is asked to correctly enter the format of a card.

* If a player enters a card he does not have or a card that cannot be played on  the top card, he is asked to enter a valid card.

* Special cards are dealt in a specific manner:
Skip. This card has the power to skip the turn of the next player.

Reverse. This card can reverse the playing order of the game.

Draw2. This card adds 2 cards in the hands of the next player.

* End of game. The game ends if one player finishes all his cards.


## Analysis of Project



