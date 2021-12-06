Feature: Enemy player move feature

  As the player
  I want the enemy player to move similar to me
  So that the game gets more competitive

  Background:
    Given player starts in cave 0
    And enemy player starts in cave 16
    And wumpus starts in cave 18
    And bat 1 starts in cave 19
    And bat 2 starts in cave 13
    And pit 1 starts in cave 3
    And pit 2 starts in cave 13

  Scenario: Enemy player moves to a linked cave after player moves
  Every time the player takes an action either move or shoot, the enemy player has 50% to move
    Given player starts in cave 9
    And enemy player starts in cave 16
    When player moves to cave 1
    Then enemy player moves from cave 16 to cave 6
    And enemy player will be at cave 6

  Scenario: Enemy player moves to a linked cave after player shoots
  Every time the player takes an action either move or shoot, the enemy player has 50% to move
    Given player starts in cave 9
    And enemy player starts in cave 16
    When player shoots at cave 1
    Then enemy player moves from cave 16 to cave 6
    And enemy player will be at cave 6

  Scenario: Enemy player moves to a cave with wumpus
  Every time the player takes an action either move or shoot, the enemy player has 50% to move
  Enemy player enters a cave with wumpus and dies
    Given player starts in cave 9
    And enemy player starts in cave 16
    And wumpus starts in cave 18
    When player moves to cave 1
    And enemy player moves from cave 16 to cave 17
    And player moves to cave 2
    And enemy player moves from cave 17 to cave 18
    Then enemy player will be dead

  Scenario: Enemy player moves to a cave with bat
  Every time the player takes an action either move or shoot, the enemy player has 50% to move
  Enemy player enters a cave with bat and teleports to another cave
    Given player starts in cave 9
    And enemy player starts in cave 11
    And bat 1 starts in cave 19
    When player moves to cave 1
    And enemy player moves from cave 11 to cave 12
    And player moves to cave 2
    And enemy player moves from cave 12 to cave 19
    And bat teleports enemy player to cave 8 and itself to cave 4
    Then enemy player will be at cave 8
    And bat 1 will be at cave 4

  Scenario: Enemy player moves to a cave with pit
  Every time the player takes an action either move or shoot, the enemy player has 50% to move
  Enemy player enters a cave with pit and dies
    Given player starts in cave 9
    And enemy player starts in cave 11
    And pit 1 starts in cave 3
    When player moves to cave 1
    And enemy player moves from cave 11 to cave 2
    And player moves to cave 2
    And enemy player moves from cave 2 to cave 3
    Then enemy player will be dead


# Enemy player move test list
#  DONE - Player shoots and enemy moves
#  DONE - Enemy moves to a random linked cave
#  DONE - Enemy moves to a random cave and gets eaten by the wumpus
#  DONE - Enemy moves to a random cave and dies inside a pit
#  DONE - Enemy moves to a random cave and bat teleports it


