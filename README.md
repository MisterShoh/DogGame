DogGame in Scala
=========================

This is a project created by Marc Baldischwieler and Shohrukh Koyirov. It is a basic scala project as used in the
class Software Engineering at the University of Applied Science HTWG Konstanz<br><br>

DogGame instructions
=========================



DOG -  Rules of the game <br>

Aim
____ 
The team me mb ers (2 x 2) sit opposite. The object of the game  is to move own men 
from home to the target area as fast as possible. They are moved in an anticlockwise 
direction along the course according to the value of the cards. The winning team is 
the one who has first placed all its 8 me n in the target area. Once a player has set 
its 4 men in the target area, he continues by supporting his partner. He moves his partners me
n according to the values of his own cards. <br>

Procedure
___________
The  cards are distributed by the same person during the whole game.
At the start of the game  each player obtains 6 cards.<br>
At each round’s start the partners exchange one card, swapped face down. 
Thereafter the obtained card joins the visible ones in hand. This feature enables the 
partners to provide each other strategically important cards.<br>
One player after the other, going anticlockwise around the table, lays down a single 
card, visible to all, and moves his man according to the card’s value (see p. 3:Card
Values).<br>
If a player’s set of cards doesn’t permit to start or move, he’s out of the current 
round and must lay down his remaining cards. A round is finished once all cards are 
laid down.<br>
At each subsequent round another player starts (anticlockwise change).
In the second round 5 cards are distributed, in the third 4, and so on, down to 2 
cards. There after the next round starts again with 6 cards, etc. (6, 5, 4, 3, 2, 6, 5, 
4,...).<br>

Start
______
A man can be moved from home to the starting-point either with an ace, a king or a 
joker (see p.1: Illustration). This point is the start and the end of the course , and 
where the men can enter the target area. When a man is moved from home to the starting
-point, he is protected, i.e. it can neither be exchanged (see p.3: JACK), returned home
 or passed (passage blocked, for own men, too).<br>

Returning home
_______________ 
If a man arrives at a position occupied by another, the latter must return home. This 
also applies to own men.If one or more men are passed at the same time by another man 
moved by a 7 or a fraction thereof, these ones must return home. A man on a foreign starting
-point must return home, if another player’s man is set to start.Men on the own starting
-point and in the target area cannot be returned home.<br>

Passing
_________
...is always permitted, except in target area or if a man is on its own starting-point.<br><br>

Compelled to move
__________________
The players are compelled to move their men the complete number of steps determinated by the card value, particularly to place the last man in the target area. If the card’s value exceeds the number of necessary steps, another lap of the course must be started to retry the entry.As a possible consequence additional laps must be turned.<br>
Exception: If the last man of a player e.g. needs 5 point to finish, but the corresponding card is lacking, he can use a 7 (if at hand) and apply the remaining 2 points for one of his partner’s men.
<br>

Target area
____________
In order to enter the target area the starting-point must be hit twice and the direction must be 
anticlockwise. E.g. a man positioned 2 steps after the starting-point can be moved 4 steps backward, but not straight into the target area. The entry might follow with the next card. Within the target area passing men is prohibited.<br>

Card Values
=============

ACE
______
	1 step forward or
	11 steps forward or
	move one man from home to starting-point

KING
______
	13 steps forward or
	move one man from home to starting-point

QUEEN
______
	12 steps forward

JACK
______
	exchange 2 men, one of which must be your own.
	A man positioned for the first time at the start, at home or in the
	target area, may not be exchanged.

10 
______
	10 steps forward

9
______
	9 steps forward

8
______
	8 steps forward

7 
______
	7 steps forward
	The 7 steps can be partitioned to a random number to use on own
	men (e.g.: 5 + 2, 3 + 2 + 2).
	A 7 step advance or a fraction thereof returns all the men he passes, 
	including his own, home.

6
______
	6 steps forward

5
______
	5 steps forward

4
______
	4 steps forward or backward

3
______
	3 steps forward

2
______
	2 steps forward

JOKER
_______
	The joker can take any of the card values mentioned above.

