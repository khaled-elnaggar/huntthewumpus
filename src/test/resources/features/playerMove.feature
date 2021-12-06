Feature: Player movement options

  As the player
  I want to be able to move across the map
  So that I can hunt the wumpus and adventure

  Background:
    Given player starts in cave 0
    And enemy player starts in cave 5
    And wumpus starts in cave 18
    And bat 1 starts in cave 19
    And bat 2 starts in cave 13
    And pit 1 starts in cave 3
    And pit 2 starts in cave 13

  Scenario Outline: Player moves to a linked cave
  The player should be allowed to move successfully to a cave that is connected to it
    Given player starts in cave <PlayerStartingCave>
    When player moves to cave <PlayerNextCave>
    Then player will be at cave <PlayerCurrentCave>
    Examples:
      | PlayerStartingCave | PlayerNextCave | PlayerCurrentCave |
      | 0                  | 7              | 7                 |
      | 0                  | 12             | 0                 |


  Scenario: Player moves to a cave with wumpus and gets eaten
  If the player enters a cave with wumpus inside it, the player should die and the game should be over
    Given player starts in cave 9
    And wumpus starts in cave 18
    When player moves to caves
      | 10 | 18 |
    Then player will be dead
    And game is lost

  Scenario: Player moves to cave with bat
  The player should change location when he enters a cave with bat, the bat will also change location
    Given player starts in cave 11
    And bat 1 starts in cave 19
    When player moves to caves
      | 12 | 19 |
    And bat teleports player to cave 8 and itself to cave 4
    Then player will be at cave 8
    And bat 1 will be at cave 4

  Scenario: Player moves to cave with pit
  If the player enters a cave with pit, the player should die and the game should be over
    Given player starts in cave 11
    And pit 1 starts in cave 3
    When player moves to caves
      | 2 | 3 |
    Then player will be dead
    And game is lost

  Scenario: Player moves to cave with pit and bat
  If the player enters a cave with pit and bat, the player should die and the game should be over
    Given player starts in cave 11
    And bat 1 starts in cave 19
    And pit 1 starts in cave 19
    When player moves to caves
      | 12 | 19 |
    Then player will be dead
    And game is lost