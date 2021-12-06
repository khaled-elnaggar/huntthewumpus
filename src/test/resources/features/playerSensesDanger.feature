Feature: Player senses danger warnings

  As the player
  I want to sense danger when nearby
  So that I can avoid them and play further

  Background:
    Given player starts in cave 0
    And enemy player starts in cave 5
    And wumpus starts in cave 18
    And bat 1 starts in cave 19
    And bat 2 starts in cave 13
    And pit 1 starts in cave 3
    And pit 2 starts in cave 13

  Scenario Outline: Player senses the dangers in a linked cave
  The player should sense the dangers when in a linked cave to his
    Given player starts in cave <PlayerStartingCave>
    When player moves to cave <PlayerNextCave>
    Then player will sense that "<WarningMessage>"

    Examples:
      | PlayerStartingCave | PlayerNextCave | WarningMessage         |
      | 9                  | 10             | there's an awful smell |
      | 16                 | 15             | you hear a rustling    |
      | 1                  | 2              | you feel a draft       |

