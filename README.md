# UNO
This repository contains the code developed by [Sushrut Patwardhan](f20190382@pilani.bits-pilani.ac.in) and [Tanishq Duhan](f20190636@pilani.bits-pilani.ac.in) for the group assignment : Build UNO, for the course Object Oriented Programming in JAVA.

## Table of Contents
* [How to Use](#how-to-use)
* [Helper Document](#helper-document)
* [Analysis of Project](#analysis-of-project)
* [Video Demonstration](https://drive.google.com/file/d/1ikV5JQsuTkYKpXAe6SbH8Y-UDVYdGrSv/view?usp=sharing)
* [Plagiarism Statement](https://docs.google.com/document/d/1lZhvOtfVRxw00LGsp8aN04sJJwQBzFvcX22fm_CzjJY/edit#)
* [UML Diagrams](8.2019B5A70382P_2019B5A70636P.pdf)


## How to Use
This repository contains multiple files and folders which contain the same code in multiple formats:
* The Folder "Build" contains the the project in bytecode.
* The File "UNO.java" contains the project in .java format and can be compiled into byteCode.
* The File "UNO.jar" contains the file in jar format and this code needs to be extracted before use. The extracted file contains a "UNO.java" file that can be can be compiled into byteCode.
* [This](https://docs.google.com/document/d/1RX_772KRHujQt6YojQMUvlOEX8ROps4PbVOHyAWTbG0/edit#) contains the Helper Document
* [This](https://docs.google.com/document/d/1O89OEHr1_zfs8FL_6dEZ22pBPBxMdebVVEota0OqsO0/edit#) contains the Analysis of the project
* [This](https://drive.google.com/file/d/1ikV5JQsuTkYKpXAe6SbH8Y-UDVYdGrSv/view?usp=sharing) contains the link to the video demonstrating the code

## [Helper Document](https://docs.google.com/document/d/1RX_772KRHujQt6YojQMUvlOEX8ROps4PbVOHyAWTbG0/edit#)

###### First the startGame method asks us to add the number of players in the game.

###### The first prompt that we get is “Enter Number of players (between 2-5):”, in which the user can enter the number of players.
* For the following example, we choose the number 2.
* Next, we take the names of all players to create and assign to the new Player Objects.

###### As soon as we enter the player details, the startGame method initializes and shuffles the deck of all cards. It then distributes 7 cards to players and fetches the top card for the play to begin. In case the top card is a special card, the deck is shuffled again and the top card is fetched again. This goes on until the top card is a normal card. 

###### At all times and for all turns, the top card is displayed. The cards of the current player are also displayed.

###### According to the rules of UNO, if the player has a card that has the same number or colour as the top card, he may play such a card, else he can draw a card. If the player draws a card and gets a valid playable card, he can play this card else he can pass his turn.
* There can be 2 scenarios for the player:
* The Player has a playable card: The player is asked to play a card.

* The player does not have a card he can play: One card is automatically drawn from the deck for the player. 

###### There can be 2 further scenarios:
* The player now has a playable card: The player is asked to play a card.

* The player still does not have a playable card: The player’s current turn is skipped automatically.

###### If the player enters the card in a different format, he is asked to correctly enter the format of a card.

###### If a player enters a card he does not have or a card that cannot be played on  the top card, he is asked to enter a valid card.

###### Special cards are dealt in a specific manner:
* Skip. This card has the power to skip the turn of the next player.

* Reverse. This card can reverse the playing order of the game.

* Draw2. This card adds 2 cards in the hands of the next player.

###### End of game. 
* The game ends if one player finishes all his cards.


## [Analysis of Project](https://docs.google.com/document/d/1O89OEHr1_zfs8FL_6dEZ22pBPBxMdebVVEota0OqsO0/edit#)
###### OOP Principles:

* Encapsulate what varies: Most important data that keeps on changing in our project are the resultOfChance, card, nextCard and alreadyDrawn properties. These actually act like callbacks too, to notify the result of a player's chance. These can be encapsulated into a reactive observable. Plenty of private methods in both Game and Player class do encapsulate the variable data.

* Favour composition over inheritance: Our code does favour inheritance in the Normal and Special Card classes as they both inherit the PlayingCard class. Instead of this, using an interface Card for both directly would have been better.

* Program to an interface not implementation: The Card interface in our code could have supported this concept. Instead of inheriting a PlayingCard class, both Normal and Special Card classes could have implemented Card interface with a card factory class returning appropriate Card class objects.

* Classes should be open for extension and closed for modification: It is difficult to make entire systems open for extension and closed for modification, but small classes can achieve this goal. The card class is open for extension as adding new methods inside the Normal/Special Card class won’t create any disturbances in the driving code. Also for modification, suppose adding a new power in the special card class can be achieved by just adding the new power in the Power enum. We do not have to change the existing code of the class yet we achieve the results. 

* Depend on abstraction, do not depend on concrete classes: Adding an abstract action layer between Player’s play method and the main loop in the startgame method could have provided more freedom for the player action. Then, integrating more player actions like calling UNO or calling caught would have been easier compared to now. Currently we will have to change the driver code as well as the player's run method for adding these actions.
 
###### Observer Pattern:
Our code tried to have the ideology of observer pattern, but lacked it’s precise implementation. The properties of the player class like resultOfChance, card, nextCard and alreadyDrawn which are also used in the Game class as well as the drawpile in the game class whose next card is required in the player class could have been abstracted out into observables. Using that would have been easier for the manipulation in the player class’s play method as well as the game class’s main driving while loop in the startgame method. Currently, our code is notifying changes to and fro buy the HashMap and the properties altered by setParameters method. If there was an Observable keeping track of the draw and discarded piles as well as the player ( who is currently playing his/her turn ) then the cluster of if-else inside the loop could have been encapsulated away. One more way would have been to register a player as an observer observing the state of draw, discard pile and turn index. There could have been an eventToState mapper continuously yielding states in the state of the game in a state stream depending on the event supplied in the event stream. This would have helped even more out when integrating the GUI. Observer pattern would have greatly improved the ability to separate out the actual business logic from the user interface logic



