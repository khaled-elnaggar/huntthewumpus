Feature: Game starts initialized correctly

  As the player
  I want the game map to be initialized with the right objects
  So that I can start playing and have fun

  Background:
    Given player starts in cave 0
    And enemy player starts in cave 5
    And wumpus starts in cave 18
    And bat 1 starts in cave 19
    And bat 2 starts in cave 13
    And pit 1 starts in cave 3
    And pit 2 starts in cave 13

  Scenario: Number of game objects initialize correctly
  Game should start with 1 player, 1 enemy player, 2 bats and 1 pit
    Given game starts
    Then there is 1 "player"
    And there is 1 "enemy player"
    And there are 2 "bats"
    And there are 2 "pits"

  Scenario: Game objects initialize in their respective caves
  Game object locations should be according to the background
    Given game starts
    Then player will be at cave 0
    And enemy player will be at cave 5
    And wumpus will be at cave 18
    And bat 1 will be at cave 19
    And bat 2 will be at cave 13
    And pit 1 will be at cave 3
    And pit 2 will be at cave 13