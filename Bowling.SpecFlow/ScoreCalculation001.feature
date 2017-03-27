Feature: Score Calculation 001
  In order to know my performance
  As a player
  I want the system to calculate my total score
  
Scenario: Gutter game 001
  Given a new bowling game
  When all of my balls are landing in the gutter
  Then my total score should be 0
  
Scenario: Beginners game 001
  Given a new bowling game
  When I roll 2 and 7
  And I roll 3 and 4
  And I roll 8 times 1 and 1
  Then my total score should be 32
  
Scenario: Another beginners game 001
  Given a new bowling game
  When I roll the following series:	2,7,3,4,1,1,5,1,1,1,1,1,1,1,1,1,1,1,5,1
  Then my total score should be 40
  
Scenario: All Strikes 001
  Given a new bowling game
  When all of my rolls are strikes
  Then my total score should be 300
  
Scenario: One single spare 001
   Given a new bowling game 
   When I roll the following series: 2,8,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
   Then my total score should be 29
   
Scenario: All spares 001
  Given a new bowling game
  When I roll 10 times 1 and 9
  And I roll 1
  Then my total score should be 110

 @ignore
 Scenario: None 001
   Given do nothing
   Then none