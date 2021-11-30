Feature: Hunt the Wumpus

  Scenario: Player shoots the wumpus
  If the player shoots a cave that contains the wumpus, the wumpus should die and game should be over
    Given player is in cave 9
    And wumpus is in cave 18
    When player moves to cave 10
    And player shoots at cave 18
    Then wumpus is dead
    And game is won

  Scenario: Player misses the wumpus and it remains asleep
  If the arrow misses the wumpus cave, the wumpus may remain a sleep and not move
    Given player is in cave 0
    And wumpus is in cave 18
    When player shoots at cave 1
    And wumpus remains asleep
    Then wumpus will be at cave 18
    And game is still on

  Scenario Outline: Player misses the wumpus and it wakes up and moves
  If the player shoots but misses the wumpus cave, the wumpus may wake up and move to a linked cave
  If player is in that cave, the wumpus will eat him
    Given player is in cave <PlayerStartingCave>
    And wumpus is in cave <WumpusStartingCave>
    When player moves to cave <PlayerNextCave>
    And player shoots at cave <CaveToShootAt>
    But wumpus will wake up from <WumpusStartingCave> and move to cave <WumpusNextCave>
    Then wumpus will be at cave <WumpusNextCave>
    And game is <GameStatus>

    Examples:
      | PlayerStartingCave | WumpusStartingCave | WumpusNextCave | PlayerNextCave | CaveToShootAt | GameStatus |
      | 9                  | 18                 | 19             | 10             | 11            | still on   |
      | 9                  | 18                 | 10             | 10             | 11            | over       |

  Scenario: Player runs out of arrows
  If the player runs out of arrows before killing the wumpus, the game is over
    Given player is in cave 0
    And player has 1 arrow remaining
    When player shoots at cave 4
    Then game is lost

  Scenario: Player shoots an arrow through multiple caves
  The player can choose up to 5 caves to shoot at
    Given player is in cave 0
    And wumpus is in cave 18
    When player shoots an arrow at caves
      | 7 | 8 | 9 | 10 | 18 |
    Then wumpus is dead
    And game is won

  Scenario: Player shoots an arrow through multiple caves but some are not linked
  If any of the caves that player shoots is not linked, the arrow goes to a random linked cave
    Given player is in cave 0
    And wumpus is in cave 18
    When player shoots an arrow at caves
      | 15 | 8 | 9 | 10 | 3 |
    But cave 15 is not linked to 0, so arrow will go to 7 instead
    And cave 3 is not linked to 10, so arrow will go to 18 instead
    Then wumpus is dead
    And game is won

  Scenario: Player shoots an arrow through multiple caves but the arrow get back to him and kills him
  If the arrow enters the player's cave, the player dies and game is over
    Given player is in cave 0
    When player shoots an arrow at caves
      | 7 | 19 |
    But cave 19 is not linked to 7, so arrow will go to 0 instead
    Then player is dead
    And game is lost
    